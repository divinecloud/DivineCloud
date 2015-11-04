package com.dc.api.cmd;

import com.dc.DcException;
import com.dc.api.support.SshClientAccessor;
import com.dc.runbook.rt.cmd.GroupTermCmdRequestType;
import com.dc.runbook.rt.cmd.IndividualCmdExecRequest;
import com.dc.runbook.rt.cmd.exec.ExecutionCallback;
import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.runbook.rt.cmd.exec.SshCommandExecutionCallback;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.ssh.SshCommandExecutor;
import com.dc.runbook.ssh.SshCommandExecutorImpl;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.batch.BatchUnitTask;

public class CmdExecTaskForCallback implements BatchUnitTask {
    private String executionId;
    private DtRunbookStep step;
    private NodeCredentials nodeCred;
    private GroupTermCallback groupTermCallback;
    private SshClientAccessor sshClientAccessor;

    public CmdExecTaskForCallback(String executionId, DtRunbookStep step, NodeCredentials nodeCred, SshClientAccessor sshClientAccessor, GroupTermCallback groupTermCallback) {
        this.executionId = executionId;
        this.step = step;
        this.nodeCred = nodeCred;
        this.groupTermCallback = groupTermCallback;
        this.sshClientAccessor = sshClientAccessor;
    }

    @Override
    public void execute() throws DcException {
        SshClient sshClient = sshClientAccessor.provide(nodeCred);
        IndividualCmdExecRequest request = prepareIndividualCmdExecRequest(sshClient);
        SshCommandExecutor executor = new SshCommandExecutorImpl();
        executor.execute(request);
    }

    private IndividualCmdExecRequest prepareIndividualCmdExecRequest(SshClient sshClient) {
        IndividualCmdExecRequest request = new IndividualCmdExecRequest();
        request.setType(GroupTermCmdRequestType.EXEC);
        request.setSshClient(sshClient);
        ExecutionCallback callback = new SshCommandExecutionCallback(nodeCred.getId(), groupTermCallback);
        request.setCallback(callback);
        request.setCommand(step);
        request.setExecutionId(executionId);
        request.setType(GroupTermCmdRequestType.EXEC);
        return request;
    }

}