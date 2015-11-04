package com.dc.runbook.rt.cmd.exec;


import com.dc.DcException;
import com.dc.runbook.rt.cmd.IndividualCmdCancelRequest;
import com.dc.runbook.ssh.SshCommandExecutor;
import com.dc.runbook.ssh.SshCommandExecutorImpl;
import com.dc.util.batch.BatchUnitTask;

public class GroupCmdCancelTask implements BatchUnitTask {

    private IndividualCmdCancelRequest cancelRequest;

    public GroupCmdCancelTask(IndividualCmdCancelRequest cancelRequest) {
        this.cancelRequest = cancelRequest;
    }

    @Override
    public void execute() throws DcException {
        SshCommandExecutor sshCommandExecutor = new SshCommandExecutorImpl();
        sshCommandExecutor.cancel(cancelRequest);
    }
}