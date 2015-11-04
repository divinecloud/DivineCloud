package com.dc.api.support;

import com.dc.DcException;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.batch.BatchUnitTask;

public class ConnectTask  implements BatchUnitTask {

    private String executionId;
    private SshClientAccessor sshClientAccessor;
    private NodeCredentials nodeCred;
    private volatile boolean success;

    public ConnectTask(String executionId, SshClientAccessor sshClientAccessor, NodeCredentials nodeCred) {
        this.executionId = executionId;
        this.sshClientAccessor = sshClientAccessor;
        this.nodeCred = nodeCred;
    }

    @Override
    public void execute() throws DcException {
        SshClient sshClient = sshClientAccessor.provide(nodeCred);
        if(sshClient != null) {
            success = true;
        }
    }

    public NodeCredentials getNodeCred() {
        return nodeCred;
    }

    public boolean isSuccess() {
        return success;
    }
}