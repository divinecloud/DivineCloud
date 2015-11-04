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
