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

package com.dc.runbook.rt.cmd.exec;


public class GroupTermExecStatusChunk {
    private String executionId;
    private int successCount;
    private int failedCount;
    private int totalCount;
    private GroupCmdExecState state;
    private boolean complete;

    private String nodeDisplayId;
    private String output;
    private boolean error;
    private GroupCmdExecState nodeState;

    private String spaceId;
    private long startTime;
    
    public GroupTermExecStatusChunk(String executionId, int successCount, int failedCount, int totalCount, GroupCmdExecState state, boolean complete, String nodeDisplayId, String output, GroupCmdExecState nodeState, boolean error, long startTime) {
    	this.executionId = executionId;
    	this.successCount = successCount;
        this.failedCount = failedCount;
        this.totalCount = totalCount;
        this.state = state;
        this.complete = complete;
        this.nodeDisplayId = nodeDisplayId;
        this.output = output;
        this.error = error;
        this.nodeState = nodeState;
        this.startTime = startTime;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public GroupCmdExecState getState() {
        return state;
    }

    public void setState(GroupCmdExecState state) {
        this.state = state;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getNodeDisplayId() {
        return nodeDisplayId;
    }

    public void setNodeDisplayId(String nodeDisplayId) {
        this.nodeDisplayId = nodeDisplayId;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public GroupCmdExecState getNodeState() {
        return nodeState;
    }

    public void setNodeState(GroupCmdExecState nodeState) {
        this.nodeState = nodeState;
    }

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
