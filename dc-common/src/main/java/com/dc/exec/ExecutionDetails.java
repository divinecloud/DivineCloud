package com.dc.exec;

import java.util.Arrays;

/**
 * Contains the ssh execution details.
 */
public class ExecutionDetails {
    private byte[] output;
    private byte[] error;
    private int statusCode;

    public ExecutionDetails(int code, byte[] output, byte[] error) {
        this.output = output;
        this.error = error;
        this.statusCode = code;
    }

    public byte[] getOutput() {
        return output;
    }

    public byte[] getError() {
        return error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isFailed() {
        return (statusCode != 0);
    }

    @Override
    public String toString() {
        return "SshExecutionDetails{" +
                "output=" + Arrays.toString(output) +
                ", error=" + Arrays.toString(error) +
                ", statusCode=" + statusCode +
                '}';
    }
}
