package com.dc.ssh.client.shell;

import com.dc.ssh.client.support.SshShellConnectInfo;
import com.dc.ssh.client.exec.vo.NodeCredentials;

public class NodeConfig {
	private String	        termId;
	private String	        spaceId;
	private NodeCredentials	nodeCredentials;
	private SshUserInfo	    sshUserInfo;

	public NodeConfig(NodeCredentials nodeCredentials) {
		this.nodeCredentials = nodeCredentials;
		sshUserInfo = new SshShellConnectInfo(nodeCredentials);
	}

	public NodeConfig(String termId, String spaceId, NodeCredentials nodeCredentials, SshUserInfo sshUserInfo) {
		this.termId = termId;
		this.spaceId = spaceId;
		this.nodeCredentials = nodeCredentials;
		this.sshUserInfo = sshUserInfo;
	}

	public String getTermId() {
		return termId;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public NodeCredentials getNodeCredentials() {
		return nodeCredentials;
	}

	public SshUserInfo getSshUserInfo() {
		return sshUserInfo;
	}
}
