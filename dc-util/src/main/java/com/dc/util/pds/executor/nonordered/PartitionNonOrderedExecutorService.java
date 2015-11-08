/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.dc.util.pds.executor.nonordered;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.util.pds.executor.AbstractPartitionExecutorService;
import com.dc.util.pds.executor.Configuration;
import com.dc.util.pds.executor.PartitionMessageHandler;
import com.dc.util.pds.executor.support.DaemonThreadFactory;
import com.dc.util.pds.executor.support.PartitionExecutorWorker;
import com.dc.util.pds.executor.support.PartitionMessage;

/**
 * Partition Executor Service implementation to support concurrent processing of the messages in different partitions.
 * The service creates fairness to avoid one partition with too many messages from taking over the thread pool, and hurting the
 * processing response of other partitions.
 * Since it even runs the messages received within the partition in parallel, it will NOT be able to maintain the order of message
 * handling within each partition. For such a use case, use PartitionOrderedExecutorService instead.
 */
public class PartitionNonOrderedExecutorService<M> extends AbstractPartitionExecutorService<M> {
    private static AtomicInteger                    serviceIdGenerator = new AtomicInteger(); //Generates unique ID per instance of this class within the JVM.
    private PartitionMessageHandler<M>              messageHandler;
    private Configuration                           configuration;

    private Map<String, Queue<PartitionMessage<M>>> messagesMap;
    private BlockingQueue<String>                   partitionIDQueue;

    private Map<String, AtomicInteger>              messageEnqueuedCountMap;
    private ExecutorCompletionService<Integer>      executorCompletionService;
    private ThreadPoolExecutor                      threadPoolExecutor;
    private LinkedBlockingQueue<Runnable>           executorServiceQueue;
    private LinkedBlockingQueue<Callable<Integer>>  pendingExecutorServiceQueue;

    private volatile boolean                        done;
    private volatile boolean                        closePending;
    private AtomicInteger                           currentMessageCount;
    private PartitionIDQueueHandler                 partitionIDQueueHandler;

    private FutureTaskCleanerThread                 futureTaskCleanerThread;

    public PartitionNonOrderedExecutorService(PartitionMessageHandler<M> messageHandler, Configuration configuration) throws IllegalArgumentException {
        this.messageHandler = messageHandler;
        this.configuration = configuration;
        validate();
        initialize();
    }

    public void close() throws InterruptedException {
        closePending = true;
        while (currentMessageCount.get() > 0 || pendingExecutorServiceQueue.size() > 0) {
            Thread.sleep(10);
        }

        done = true;
        partitionIDQueueHandler.interrupt();
        futureTaskCleanerThread.interrupt();
        threadPoolExecutor.shutdown();

        while (!threadPoolExecutor.isTerminated()) {
            Thread.sleep(10);
        }
    }

    public void closeNow() {
        closePending = true;
        done = true;
        partitionIDQueueHandler.interrupt();
        futureTaskCleanerThread.interrupt();
        threadPoolExecutor.shutdownNow();
    }

    public void submit(String partitionId, M message) throws IllegalArgumentException, InterruptedException {
        if (message == null || partitionId == null || partitionId.trim().length() == 0) {
            throw new IllegalArgumentException("Partition ID, Message cannot be NULL. partitionId : " + partitionId + " Message : " + message);
        }
        partitionId = partitionId + partitionPostfix;

        PartitionMessage<M> partitionMessage = new PartitionMessage<M>(partitionId, message);

        if (!closePending) {
            synchronized (partitionMessage.getPartitionID().intern()) {
                Queue<PartitionMessage<M>> queue = messagesMap.get(partitionMessage.getPartitionID());
                if (queue == null) {
                    queue = new ConcurrentLinkedQueue<PartitionMessage<M>>();
                    messagesMap.put(partitionMessage.getPartitionID(), queue);
                }

                queue.add(partitionMessage);
                partitionIDQueue.put(partitionMessage.getPartitionID());
            }
            currentMessageCount.incrementAndGet();
        } else {
            throw new InterruptedException("Queue is being closed, no more messages can be added");
        }
    }

    private void validate() throws IllegalArgumentException {
        if (configuration == null || messageHandler == null) {
            throw new IllegalArgumentException("messageHandler, configuration cannot be NULL");
        }

        if (configuration.getMinNoOfThreads() < 2 || configuration.getMaxNoOfThreads() < 2) {
            throw new IllegalArgumentException("Both Min and Max No. Of Threads cannot be less than 2");
        }

        if (configuration.getExpiryTimeInSecs() < 1) {
            throw new IllegalArgumentException("Expiry time cannot be less than one second.");
        }

        if (configuration.getMessageBatchSize() < 1) {
            throw new IllegalArgumentException("Msg Batch Size cannot be less than 1.");
        }
    }

    private void initialize() {
        int id = serviceIdGenerator.incrementAndGet();
        currentMessageCount = new AtomicInteger();
        executorServiceQueue = new LinkedBlockingQueue<Runnable>(configuration.getThreadIncreaseThreshold());
        pendingExecutorServiceQueue = new LinkedBlockingQueue<Callable<Integer>>();
        if (configuration.isImplicitClose()) {
            threadPoolExecutor = new ThreadPoolExecutor(configuration.getMinNoOfThreads(), configuration.getMaxNoOfThreads(), configuration.getIdleThreadCleanupInSecs(), TimeUnit.SECONDS, executorServiceQueue, new DaemonThreadFactory("PartitionedExecutorPooledThread-" + id));
        } else {
            threadPoolExecutor = new ThreadPoolExecutor(configuration.getMinNoOfThreads(), configuration.getMaxNoOfThreads(), configuration.getIdleThreadCleanupInSecs(), TimeUnit.SECONDS, executorServiceQueue);
        }

        executorCompletionService = new ExecutorCompletionService<Integer>(threadPoolExecutor);
        messagesMap = new ConcurrentHashMap<String, Queue<PartitionMessage<M>>>();
        partitionIDQueue = new LinkedBlockingQueue<String>();
        partitionIDQueueHandler = new PartitionIDQueueHandler();
        messageEnqueuedCountMap = new ConcurrentHashMap<String, AtomicInteger>();
        partitionIDQueueHandler.setName("PartitionIDQueueHandler-" + id);
        partitionIDQueueHandler.setDaemon(true);
        partitionIDQueueHandler.start();
        futureTaskCleanerThread = new FutureTaskCleanerThread();
        futureTaskCleanerThread.start();
    }

    private class PartitionIDQueueHandler extends Thread {

        public void run() {
            while (!done) {
                PartitionExecutorWorker<M> queueWorker = null;
                try {
                    while (pendingExecutorServiceQueue.size() > 0) {
                        handlePendingWork();
                    }
                    String partitionID = poll();

                    if (partitionID != null) {
                        boolean addToService = incrementPartitionMessageCount(partitionID);

                        if (addToService) {
                            queueWorker = new PartitionExecutorWorker<M>(messageHandler, partitionID, executorServiceQueue, pendingExecutorServiceQueue, configuration, messagesMap, messageEnqueuedCountMap, currentMessageCount);

                            executorCompletionService.submit(queueWorker);
                        } //else do nothing
                    } //else do nothing
                } catch (InterruptedException e) {
                    handleException(e);
                } catch (RejectedExecutionException e) {
                    if (!done) {
                        blockTillWorkerAvailable(queueWorker);
                    } //else do nothing
                } catch (Throwable t) {
                    handleException(t);
                }
            }
        }
    }

    private void handlePendingWork() {
        int availableCount = configuration.getThreadIncreaseThreshold() - executorServiceQueue.size();
//        DcLoggerFactory.getInstance().getLogger().info("Available Count: " + availableCount);
//        DcLoggerFactory.getInstance().getLogger().info("PendingExecutorServiceQueue Size : " + pendingExecutorServiceQueue.size());
        if (availableCount > 0) {
            for (int i = 0; i < availableCount; i++) {
                Callable<Integer> callable = pendingExecutorServiceQueue.poll();
                if (callable != null) {
                    executorCompletionService.submit(callable);
                } else {
                    break;
                }
            }
        }
    }

    private String poll() throws InterruptedException {
        String partitionID;
        if (executorServiceQueue.size() > 0 || pendingExecutorServiceQueue.size() > 0) {
            partitionID = partitionIDQueue.poll();
        } else {
            partitionID = partitionIDQueue.poll(configuration.getExpiryTimeInSecs(), TimeUnit.SECONDS);
        }
        return partitionID;
    }

    private boolean incrementPartitionMessageCount(String partitionID) {
        boolean addToService = false;
        synchronized (partitionID.intern()) {
            AtomicInteger messageCount = messageEnqueuedCountMap.get(partitionID);
            if (messageCount == null || messageCount.get() == 0 || (configuration.getMaxNoOfThreads() - threadPoolExecutor.getActiveCount() > 0)) {
                if (messageCount == null) {
                    messageCount = new AtomicInteger();
                }
                messageEnqueuedCountMap.put(partitionID, messageCount);
                addToService = true;
            }
            messageCount.incrementAndGet();
        }
        return addToService;
    }

    private void blockTillWorkerAvailable(PartitionExecutorWorker<M> queueWorker) {
        if (!done) {
            //The Executor Service queue is maxed out. Wait for some time till few messages are processed
            while (executorServiceQueue.size() >= configuration.getThreadIncreaseThreshold()) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ie) {
                    handleException(ie);
                }
            }
            executorCompletionService.submit(queueWorker);
        }
    }

    private void handleException(Throwable t) {
        if (!done) {
            t.printStackTrace();
            //@TODO: Indication of potential code bug. Give more thought on whether to log about this exception or will the stack trace suffice?
        } else {
            //close method called, so this exception could occur during the close, hence ignored.
        }
    }

    private class FutureTaskCleanerThread extends Thread {
        public void run() {
            while (!done) {
                try {
                    //We only need to remove the future object from executor service, though we dont really need the future object for any other purpose.
                    executorCompletionService.take();
                } catch (InterruptedException e) {
                    if (!done) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}