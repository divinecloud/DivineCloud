package com.dc.ssh.client.sftp;

public interface SftpCallback {

    public void done();

    public void done(SftpClientException cause);

    public int getStatusCode();

    public boolean isCancelled();

    public boolean isDone();

    public int precentageComplete(int count);

    public void execId(String processId);

    public SftpClientException getCause();

}
