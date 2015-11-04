package com.dc.api.cmd;

import com.dc.DcException;
import com.dc.api.exec.NodeExecutionDetails;
import com.dc.api.support.SshClientAccessor;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.batch.BatchUnitTask;

public class CmdExecTask  implements BatchUnitTask {
    private SshClientAccessor sshClientAccessor;
    private NodeCredentials nodeCred;
    private String command;
    private NodeExecutionDetails details;

    public CmdExecTask(SshClientAccessor sshClientAccessor, NodeCredentials nodeCred, String command) {
        this.sshClientAccessor = sshClientAccessor;
        this.nodeCred = nodeCred;
        this.command = command;
    }

    public NodeExecutionDetails getResult() {
        return details;
    }

    @Override
    public void execute() throws DcException {
        SshClient sshClient = sshClientAccessor.provide(nodeCred);
        ExecutionDetails execDetails = sshClient.execute(command);
        details = new NodeExecutionDetails(nodeCred, execDetails);
    }
}