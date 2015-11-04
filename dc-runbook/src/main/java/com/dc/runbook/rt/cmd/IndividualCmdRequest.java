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
