package com.dc.runbook.ssh;

import com.dc.runbook.rt.cmd.IndividualCmdRequest;

public interface SshCommandExecutor {
	public void execute(IndividualCmdRequest request);

	public void cancel(IndividualCmdRequest request);
}
