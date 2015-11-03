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

package com.dc.runbook.rt.cmd;

import com.dc.runbook.rt.cmd.exec.ExecutionCallback;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.ssh.client.exec.SshClient;

public class IndividualCmdExecRequest extends IndividualCmdRequest {
	private DtRunbookStep command;
	private ExecutionCallback	  callback;
    private SshClient sshClient;
	

	public ExecutionCallback getCallback() {
		return callback;
	}

	public void setCallback(ExecutionCallback callback) {
		this.callback = callback;
	}

	public DtRunbookStep getCommand() {
		return command;
	}

	public void setCommand(DtRunbookStep command) {
		this.command = command;
	}

    @Override
    public SshClient getSshClient() {
        return sshClient;
    }

    @Override
    public void setSshClient(SshClient sshClient) {
        this.sshClient = sshClient;
    }
}
