package com.dc.ssh.client.shell;

public interface OutputCallback {
    public void output(byte[] bytes);

    public void done();

    public void error(String errorMessage);
}
