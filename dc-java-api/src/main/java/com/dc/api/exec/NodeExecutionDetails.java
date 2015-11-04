package com.dc.api.exec;


import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.exec.vo.NodeCredentials;

public class NodeExecutionDetails {
    private NodeCredentials nodeCredentials;
    private ExecutionDetails executionDetails;

    public NodeExecutionDetails(NodeCredentials nodeCredentials, ExecutionDetails executionDetails) {
        this.nodeCredentials = nodeCredentials;
        this.executionDetails = executionDetails;
    }

    public NodeCredentials getNodeCredentials() {
        return nodeCredentials;
    }

    public ExecutionDetails getExecutionDetails() {
        return executionDetails;
    }
}
