package com.dc.ssh.client.support.callback;

public interface LocalCallback {
    public void output(byte[] output);

    public void error(byte[] error);

    public void done(int status);

    public int status();

    public byte[] getOutput();

    public byte[] getError();

    public void executionCancelled();

    public boolean isCancelled();
}
