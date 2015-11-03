/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.runbook.rt.cmd;

import com.dc.node.NodeDetails;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.rt.domain.NodeDto;
import com.dc.ssh.client.support.SshCredentialInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCommandRequest {
	private String	                executionId;
	private GroupTermCmdRequestType	type;
	private List<NodeDto>	        nodes;
	private List<SshCredentialInfo>	sshCredentialInfoList;
	private CommandOutputType	    commandOutputType	= CommandOutputType.RAW;
	private Date	                requestTime;
	private DtRunbookStep command;
	private List<NodeDetails>	        transientNodes;
	
	public List<SshCredentialInfo> getSshCredentialInfoList() {
		return sshCredentialInfoList;
	}

	public void setSshCredentialInfoList(List<SshCredentialInfo> sshCredentialInfoList) {
		this.sshCredentialInfoList = sshCredentialInfoList;
	}

	public CommandOutputType getCommandOutputType() {
		return commandOutputType;
	}

	public void setCommandOutputType(CommandOutputType commandOutputType) {
		this.commandOutputType = commandOutputType;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public GroupTermCmdRequestType getType() {
		return type;
	}

	public void setType(GroupTermCmdRequestType type) {
		this.type = type;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public List<NodeDto> getNodes() {
		return nodes;
	}

	public void setNodes(List<NodeDto> nodes) {
		this.nodes = nodes;
	}

	public DtRunbookStep getCommand() {
		return command;
	}

	public void setCommand(DtRunbookStep command) {
		this.command = command;
	}

	public List<NodeDetails> getTransientNodes() {
		return transientNodes;
	}

	public void setTransientNodes(List<NodeDetails> transientNodes) {
		this.transientNodes = transientNodes;
	}

}
