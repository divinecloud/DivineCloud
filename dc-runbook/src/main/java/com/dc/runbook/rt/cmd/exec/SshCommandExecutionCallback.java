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

public class SshCommandExecutionCallback implements ExecutionCallback {

	private String	                      displayId;
    private GroupTermCallback                 groupTermCallback;
    private volatile boolean done;

	public SshCommandExecutionCallback(String displayId, GroupTermCallback groupTermCallback) {
		this.displayId = displayId;
        this.groupTermCallback = groupTermCallback;
	}

	@Override
	public void outputData(byte[] output) {
		if (output != null) {
            groupTermCallback.output(displayId, new String(output));
		}
	}

	@Override
	public void errorData(byte[] output) {
		if (output != null) {
            groupTermCallback.error(displayId, new String(output));
		}
	}

	@Override
	public void done(CommandExecutionResult result) {
        done = true;
        groupTermCallback.complete(displayId, result.getCode());
	}

    @Override
    public boolean isDone() {
        return done;
    }
}
