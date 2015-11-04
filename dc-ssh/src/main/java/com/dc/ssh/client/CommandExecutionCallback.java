package com.dc.ssh.client;


public interface CommandExecutionCallback {

    public void outputData(byte[] output);

    public void errorData(byte[] output);

    public void done(int statusCode);

    public void done(SshException cause);

    public int getStatusCode();

    public boolean isCancelled();

    public void executionCancelled();

    public SshException getCause();
}
