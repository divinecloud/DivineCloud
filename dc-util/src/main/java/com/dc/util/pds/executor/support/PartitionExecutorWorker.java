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

package com.dc.util.pds.executor.support;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.util.pds.executor.Configuration;
import com.dc.util.pds.executor.PartitionMessageHandler;

/**
 * Does the actual work of consuming the message submitted to the executor service.
 */
public class PartitionExecutorWorker<M> implements Callable<Integer> {

    private PartitionMessageHandler<M> handler;
    private String partitionID;
    private LinkedBlockingQueue<Runnable> executorServiceQueue;
    private LinkedBlockingQueue<Callable<Integer>> pendingExecutorServiceQueue;
    private Configuration configuration;
    private Map<String, Queue<PartitionMessage<M>>> messagesMap;
    private Map<String, AtomicInteger> messageEnqueuedCountMap;
    private AtomicInteger currentMessageCount;

    public PartitionExecutorWorker(PartitionMessageHandler<M> handler, String partitionID, LinkedBlockingQueue<Runnable> executorServiceQueue,
                                   LinkedBlockingQueue<Callable<Integer>> pendingExecutorServiceQueue, Configuration configuration,
                                   Map<String, Queue<PartitionMessage<M>>> messagesMap, Map<String, AtomicInteger> messageEnqueuedCountMap, AtomicInteger currentMessageCount) {

        this.handler = handler;
        this.partitionID = partitionID;
        this.executorServiceQueue = executorServiceQueue;
        this.configuration = configuration;
        this.messagesMap = messagesMap;
        this.messageEnqueuedCountMap = messageEnqueuedCountMap;
        this.pendingExecutorServiceQueue = pendingExecutorServiceQueue;
        this.currentMessageCount = currentMessageCount;
    }

    public Integer call() throws Exception {
        int msgAllowedCount = configuration.getMessageBatchSize();
        int msgProcessedCount = 0;
        Queue<PartitionMessage<M>> queue = messagesMap.get(partitionID);
        PartitionMessage<M> message;
        boolean done = false;
        while(!done) {
            if(msgAllowedCount == 0 && (executorServiceQueue.size() > 0)) {
                done = true;
            }

            message = queue.poll();
            if(message == null) {
                done = true;
            }
            else {
                try {
                    handler.handle(message.getPartitionID().substring(0, message.getPartitionID().lastIndexOf('-')), message.getMessage());
                }
                catch(Throwable t) {
                    // This Stack Trace is intentionally printed. Users of this class are expected to catch the throwable
                    // in their handler class, to ensure the throwable never reaches till this line.
                    t.printStackTrace();
                }
                finally {
                    currentMessageCount.decrementAndGet();
                }
                msgAllowedCount -=1;
                msgProcessedCount +=1;
            }
        }

        boolean putBack = false;
        synchronized(partitionID.intern()) {
            AtomicInteger count = messageEnqueuedCountMap.get(partitionID);
            if(count != null && count.get() > 0) {
                count.set(count.get() - msgProcessedCount);
                if(count.get() > 0) {
                    putBack = true;
                }
            }
            else {
                messageEnqueuedCountMap.remove(partitionID);
                Queue<PartitionMessage<M>> msgQueue = messagesMap.get(partitionID);
                if(msgQueue != null && msgQueue.size() == 0) {
                    messagesMap.remove(partitionID);
                }
            }
        }

        if(putBack) {
            pendingExecutorServiceQueue.put(this);
        }
        return msgProcessedCount;
    }
}
