package com.dc.ssh.client.support.callback;

import com.dc.ssh.client.CommandExecutionCallback;

public class DirectLocalCallback implements LocalCallback {
    private CommandExecutionCallback callback;
    private int statusCode;
    private boolean cancelled;

    public DirectLocalCallback(CommandExecutionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void output(byte[] output) {
        callback.outputData(output);
    }

    public void error(byte[] error) {
        callback.errorData(error);
    }

    public void done(int status) {
        statusCode = status;
        callback.done(status);
    }

    @Override
    public void executionCancelled() {
        cancelled = true;
        callback.executionCancelled();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public int status() {
        return statusCode;
    }

    public byte[] getOutput() {
        throw new UnsupportedOperationException("This method not supported for DirectLocalCallback implementation");
    }

    public byte[] getError() {
        throw new UnsupportedOperationException("This method not supported for DirectLocalCallback implementation");
    }
}