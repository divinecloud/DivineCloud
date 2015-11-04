package com.dc.ssh.client.shell;

import com.dc.ssh.client.SshException;

public interface SshShell {

    public void close();

    public String getId();

    public boolean isConnected();

    public OutputCallback getCallback();

    public void write(byte[] bytes) throws SshException;

    public void resize(int col, int row, int wp, int hp);

}
