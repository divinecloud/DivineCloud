/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
