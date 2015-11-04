package com.dc.ssh.batch.sftp;

import java.util.concurrent.*;

public class SftpBatchExecutorService {

    private ExecutorCompletionService<String> executorCompletionService;
    private FutureTaskCleanerThread	          futureTaskCleanerThread;
    protected ThreadPoolExecutor threadPoolExecutor;
    private volatile boolean	              done;
    private String executionId;
    private int threadsCount;
    private TaskCompleteNotification taskCompleteNotification;

    public SftpBatchExecutorService(String executionId, int threadsCount, TaskCompleteNotification taskCompleteNotification, boolean blockWhenFull) {
        this.executionId = executionId;
        this.threadsCount = threadsCount;
        this.taskCompleteNotification = taskCompleteNotification;
        initialize(blockWhenFull);
    }

    public void submit(Callable<String> work) {
        executorCompletionService.submit(work);
    }

    public void close() {
        done = true;
        futureTaskCleanerThread.interrupt();
        threadPoolExecutor.shutdownNow();
    }

    private void initialize(boolean blockWhenFull) {
        LinkedBlockingQueue<Runnable> executorServiceQueue = null;
        if(blockWhenFull) {
            executorServiceQueue = new LinkedBlockingQueue<>(threadsCount);
        }
        else {
            executorServiceQueue = new LinkedBlockingQueue<>();
        }

        threadPoolExecutor = new ThreadPoolExecutor(threadsCount, threadsCount, 600, TimeUnit.SECONDS, executorServiceQueue);
        executorCompletionService = new ExecutorCompletionService<>(threadPoolExecutor);
        futureTaskCleanerThread = new FutureTaskCleanerThread();
        futureTaskCleanerThread.start();
    }

    private class FutureTaskCleanerThread extends Thread {
        public void run() {
            while (!done) {
                try {
                    executorCompletionService.take();
                    Notifier notifier = new Notifier();
                    notifier.start();
                } catch (InterruptedException e) {

                    if (!done) {
                        e.printStackTrace();
                        break;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    break;
                }
            }
        }
    }

    private class Notifier extends Thread {

        public void run() {
            taskCompleteNotification.taskComplete(executionId);
        }
    }
}
