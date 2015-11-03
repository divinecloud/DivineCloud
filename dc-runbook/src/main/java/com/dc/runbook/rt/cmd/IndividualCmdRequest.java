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

import com.dc.ssh.client.exec.SshClient;

public abstract class IndividualCmdRequest {
	private String	                executionId;
	private GroupTermCmdRequestType	type;
	private SshClient sshClient;

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public GroupTermCmdRequestType getType() {
		return type;
	}

	public void setType(GroupTermCmdRequestType type) {
		this.type = type;
	}

	public SshClient getSshClient() {
		return sshClient;
	}

	public void setSshClient(SshClient sshClient) {
		this.sshClient = sshClient;
	}

}
