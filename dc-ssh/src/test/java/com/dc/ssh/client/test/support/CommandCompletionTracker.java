package com.dc.ssh.client.test.support;


import com.dc.ssh.client.exec.SshClient;

public class CommandCompletionTracker extends Thread {

    private SampleCallback callback;
    private SshClient sshClient;
    private long cancelAfter;
    private volatile boolean cancel;
    private String executionId;

    public CommandCompletionTracker(SampleCallback callback) {
        this.callback = callback;
    }

    public CommandCompletionTracker(SampleCallback callback, SshClient sshClient, long cancelAfter, String executionId) {
        this.callback = callback;
        this.sshClient = sshClient;
        this.cancelAfter = cancelAfter;
        this.executionId = executionId;
        cancel = true;
    }

    public void run() {
        long passedTime = 0;
        if(callback != null) {
            while(!callback.isDone()) {
                try {
                    Thread.sleep(25);
                    passedTime += 25;
                    if(cancel) {
                        if(passedTime >= cancelAfter) {
                            sshClient.cancel(executionId);
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Command execution complete");
        }
    }
}
