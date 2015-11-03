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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.runbook.rt.cmd.GroupCommandRequest;

public class GroupTermExecStatus {
    private String	             execId;
    private GroupCommandRequest       cmdExecRequest;
    private volatile boolean	 complete;
    private volatile boolean     cancelled;
    private GroupCmdExecState    state;
    private long	             startTime;
    private long	             endTime;

    private AtomicInteger successCount;
    private AtomicInteger failedCount;
    private AtomicInteger totalCount;

    private List<String> successList;
    private List<String>	failedList;

    private Map<String, String>	outputMap;
    private Map<String, String>	errorMap;

    public GroupTermExecStatus() {
        initialize();
    }
    
    public GroupTermExecStatus(GroupCommandRequest cmdExecRequest) {
        initialize();
        this.cmdExecRequest = cmdExecRequest;
        this.execId = cmdExecRequest.getExecutionId();
        this.totalCount.set(cmdExecRequest.getNodes().size());
    }


    private void initialize() {
        outputMap = new ConcurrentHashMap<>();
        errorMap = new ConcurrentHashMap<>();
        state = GroupCmdExecState.RUNNING;
        successList = new Vector<>();
        failedList = new Vector<>();
        successCount = new AtomicInteger();
        failedCount = new AtomicInteger();
        totalCount = new AtomicInteger();
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public GroupCommandRequest getCmdExecRequest() {
		return cmdExecRequest;
	}

	public void setCmdExecRequest(GroupCommandRequest cmdExecRequest) {
		this.cmdExecRequest = cmdExecRequest;
	}

	public int getSuccessCount() {
        return successCount.get();
    }

    public void setSuccessCount(int count) {
        successCount.set(count);
    }

    public int getFailedCount() {
        return failedCount.get();
    }

    public void setFailedCount(int count) { failedCount.set(count); }

    public int getTotalCount() {
        return totalCount.get();
    }

    public void setTotalCount(int count) {
        totalCount.set(count);
    }

    public List<String> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<String> successList) {
        this.successList = successList;
    }

    public List<String> getFailedList() {
        return failedList;
    }

    public void setFailedList(List<String> failedList) {
        this.failedList = failedList;
    }

    public Map<String, String> getOutputMap() {
        return outputMap;
    }

    public void setOutputMap(Map<String, String> outputMap) {
        this.outputMap = outputMap;
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(Map<String, String> errorMap) {
        this.errorMap = errorMap;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void successfull(String nodeDisplayId) {
        synchronized (nodeDisplayId.intern()) {
            if(!successList.contains(nodeDisplayId)) {
                successCount.incrementAndGet();
                successList.add(nodeDisplayId);
            }
        }
        System.out.println("Node : " + nodeDisplayId + " - SUCCESS");
    }

    public void failed(String nodeDisplayId) {
        synchronized (nodeDisplayId.intern()) {
            if(!failedList.contains(nodeDisplayId)) {
                failedCount.incrementAndGet();
                failedList.add(nodeDisplayId);
            }
        }
        System.out.println("Node : " + nodeDisplayId + " - FAILED");
    }

    public void addOutput(String displayId, String outputChunk, boolean error) {
        if(!error) {
            if(!outputMap.containsKey(displayId)) {
                outputMap.put(displayId, outputChunk);
            }
            else {
                outputMap.put(displayId, outputMap.get(displayId) + outputChunk);
            }
        }
        else {
            if(!errorMap.containsKey(displayId)) {
                errorMap.put(displayId, outputChunk);
            }
            else {
                errorMap.put(displayId, errorMap.get(displayId) + outputChunk);
            }

        }
    }

    public synchronized boolean verifyIfComplete() {
        if(totalCount.get() == (successCount.get() + failedCount.get())) {
            GroupCmdExecState state;
            if(cancelled) {
                state = GroupCmdExecState.CANCELLED;
            }
            else if(failedCount.get() > 0) {
                state = GroupCmdExecState.FAILED;
            }
            else {
                state = GroupCmdExecState.SUCCESSFUL;
            }
            markComplete(state);
        }
        return complete;
    }

    private void markComplete(GroupCmdExecState  state) {
        this.state = state;
        complete = true;
        endTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "GroupTermExecStatus{" +
                "execId='" + execId + '\'' +
                ", cmdExecRequest=" + cmdExecRequest +
                ", complete=" + complete +
                ", cancelled=" + cancelled +
                ", state=" + state +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", successCount=" + successCount +
                ", failedCount=" + failedCount +
                ", totalCount=" + totalCount +
                ", successList size=" + successList.size() +
                ", failedList size=" + failedList.size() +
                '}';
    }

    public synchronized void trimOutput() {
        Set<String> keys = outputMap.keySet();
        Map<String, String> map = new ConcurrentHashMap<>();
        int allowedOutputSize = maxAllowedOutputSize();
        if(keys.size() > 0) {
            int allowedOutputSizePerNode = allowedOutputSize / keys.size();
            for (String key : keys) {
                String output = outputMap.get(key);
                if (output.length() > allowedOutputSizePerNode) {
                    output = output.substring(0, allowedOutputSizePerNode);
                }
                map.put(key, output);
            }
            outputMap = map;
        }
    }

    private int maxAllowedOutputSize() {
        int bytesCount = 4194304;
        int charCount = 4194304 / 2;
        return charCount - 100;
    }
}
