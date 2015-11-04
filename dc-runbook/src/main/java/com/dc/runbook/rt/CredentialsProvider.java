package com.dc.runbook.rt;

import com.dc.node.NodeDetails;
import com.dc.ssh.client.exec.vo.NodeCredentials;

public interface CredentialsProvider {
    public NodeCredentials provide(NodeDetails nodeDetails);
}
