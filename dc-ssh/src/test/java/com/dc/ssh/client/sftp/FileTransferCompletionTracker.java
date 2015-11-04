package com.dc.ssh.client.sftp;

public class FileTransferCompletionTracker extends Thread {
    private SftpCallback callback;

    public FileTransferCompletionTracker(SftpCallback callback) {
        this.callback = callback;
    }

    public void run() {
        if(callback != null) {
            while(!callback.isDone()) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("File Transfer complete");
        }
    }
}
