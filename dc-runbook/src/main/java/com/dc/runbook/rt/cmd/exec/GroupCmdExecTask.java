package com.dc.runbook.rt.cmd.exec;

import com.dc.DcException;
import com.dc.runbook.rt.cmd.GroupTermCmdRequestType;
import com.dc.runbook.rt.cmd.IndividualCmdExecRequest;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.ssh.SshCommandExecutor;
import com.dc.runbook.ssh.SshCommandExecutorImpl;
import com.dc.ssh.client.exec.SshClient;
import com.dc.util.batch.BatchUnitTask;

public class GroupCmdExecTask implements BatchUnitTask {
    private String executionId;
    private DtRunbookStep step;
    private String nodeId;
    private GroupTermCallback	groupTermCallback;
    private SshClient sshClient;

    public GroupCmdExecTask(String executionId, DtRunbookStep step, String nodeId, SshClient sshClient, GroupTermCallback groupTermCallback) {
        this.executionId = executionId;
        this.step = step;
        this.nodeId = nodeId;
        this.groupTermCallback = groupTermCallback;
        this.sshClient = sshClient;
    }

    @Override
    public void execute() throws DcException {
        IndividualCmdExecRequest request = prepareIndividualCmdExecRequest();
        SshCommandExecutor executor = new SshCommandExecutorImpl();
        executor.execute(request);
    }

    private IndividualCmdExecRequest prepareIndividualCmdExecRequest() {
        IndividualCmdExecRequest request = new IndividualCmdExecRequest();
        request.setType(GroupTermCmdRequestType.EXEC);
        request.setSshClient(sshClient);
        ExecutionCallback callback = new SshCommandExecutionCallback(nodeId, groupTermCallback);
        request.setCallback(callback);
        request.setCommand(step);
        request.setExecutionId(executionId);
        request.setType(GroupTermCmdRequestType.EXEC);
        return request;
    }

}
