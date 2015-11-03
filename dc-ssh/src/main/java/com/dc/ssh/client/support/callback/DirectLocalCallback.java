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