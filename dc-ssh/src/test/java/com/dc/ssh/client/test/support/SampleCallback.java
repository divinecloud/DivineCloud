/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.ssh.client.test.support;

import com.dc.ssh.client.SshException;
import com.dc.ssh.client.CommandExecutionCallback;

import java.util.List;
import java.util.Vector;

public class SampleCallback implements CommandExecutionCallback {
    private final List<byte[]> output;
    private final List<byte[]> error;
    private int statusCode;
    private String processId;
    private volatile boolean done;
    private SshException cause;
    private boolean cancelled;

    public SampleCallback() {
        output = new Vector<>();
        error = new Vector<>();
    }

    @Override
    public void outputData(byte[] bytes) {
        synchronized (output) {
            output.add(bytes);
        }
    }

    @Override
    public void errorData(byte[] bytes) {
        synchronized (error) {
            error.add(bytes);
        }
    }

    @Override
    public void done(int statusCode) {
        this.statusCode = statusCode;
        done = true;
        System.out.println("Callback Done called : " + System.currentTimeMillis());
    }

    @Override
    public void done(SshException cause) {
        this.cause = cause;
        cause.printStackTrace();
        done = true;
    }

    @Override
    public void executionCancelled() {
        cancelled = true;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }


    @Override
    public SshException getCause() {
        return cause;
    }

    public String getProcessId() {
        return processId;
    }

    public boolean isDone() {
        return done;
    }

    public byte[] getOutput() {
        byte[] outputBytes;
        synchronized (output) {
            int size = calcuateOutputSize();
            outputBytes = new byte[size];
            int i = 0;
            for(byte[] bytes: output) {
                for(byte b: bytes) {
                    outputBytes[i++] = b;
                }
            }
        }
        return outputBytes;
    }

    private int calcuateOutputSize() {
        int result = 0;
        for(byte[] bytes: output) {
            if(bytes != null) {
                result += bytes.length;
            }
        }
        return result;
    }

    private int calcuateErrorSize() {
        int result = 0;
        for(byte[] bytes: error) {
            if(bytes != null) {
                result += bytes.length;
            }
        }
        return result;
    }
    public byte[] getError() {
        byte[] errorBytes;
        synchronized (error) {
            int size = calcuateErrorSize();
            errorBytes = new byte[size];
            int i = 0;
            for(byte[] bytes: error) {
                for(byte b: bytes) {
                    errorBytes[i++] = b;
                }
            }
        }
        return errorBytes;
    }
}
