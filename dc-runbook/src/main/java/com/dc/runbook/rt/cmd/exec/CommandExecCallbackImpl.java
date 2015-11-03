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

package com.dc.runbook.rt.cmd.exec;

import com.dc.ssh.client.SshException;
import com.dc.ssh.client.CommandExecutionCallback;

public class CommandExecCallbackImpl implements CommandExecutionCallback {
	private ExecutionCallback	callback;
	private int	              statusCode;
    private boolean cancelled;

	public CommandExecCallbackImpl(ExecutionCallback callback) {
		this.callback = callback;
	}

	@Override
	public void outputData(byte[] output) {
		callback.outputData(output);
	}

	@Override
	public void errorData(byte[] error) {
		callback.errorData(error);
	}

	@Override
	public void done(int statusCode) {
		this.statusCode = statusCode;
		callback.done(new CommandExecutionResult.Builder().code(statusCode).build());
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
	public void done(SshException cause) {
		cause.printStackTrace();
		callback.done(new CommandExecutionResult.Builder().failed(true).build());
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public SshException getCause() {
		return null;
	}

}
