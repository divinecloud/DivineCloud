package com.dc.ssh.client.support.callback;

import java.util.ArrayList;
import java.util.List;

public class BufferedLocalCallback implements LocalCallback {
    private List<byte[]> outputBytes;

    private List<byte[]> errorBytes;

    private int status = -99999999;

    private boolean cancelled;

    public BufferedLocalCallback() {
        outputBytes = new ArrayList<>(2);
        errorBytes = new ArrayList<>(2);
    }

    public void output(byte[] output) {
        outputBytes.add(output);
    }

    public void error(byte[] error) {
        errorBytes.add(error);
    }

    public void done(int status) {
        this.status = status;
    }

    public int status() {
        return status;
    }

    @Override
    public void executionCancelled() {
        cancelled = true;
    }

    public byte[] getOutput() {
        return cloneBytes(outputBytes);
    }

    public byte[] getError() {
        return cloneBytes(errorBytes);
    }

    private int calculateBytesSize(List<byte[]> bytesList) {
        int result = 0;
        for(byte[] bytes : bytesList) {
            result += bytes.length;
        }
        return result;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    private byte[] cloneBytes(List<byte[]> bytesList) {
        int size = calculateBytesSize(bytesList);
        byte[] result = new byte[size];
        int index = 0;
        for(byte[] bytes:bytesList) {
            System.arraycopy(bytes, 0, result, index, bytes.length);
            index +=bytes.length;
        }
        return result;
    }
}