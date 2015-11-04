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

package com.dc.runbook.rt.exec.output;


import com.dc.runbook.rt.exec.ExecState;
import com.dc.runbook.rt.exec.RunbookItemStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StepExecutionStatus implements RunbookItemStatus {

    private String	                 itemId;
    private boolean	             complete;
    private ExecState state;
    private long	             startTime;
    private long	             endTime;
    private Map<String, Integer> nodeStatusCodeMap;
    private Map<String, Boolean>	nodeStatusMap;
    private Map<String, String> nodeStatusMessageMap;

    public StepExecutionStatus(String itemId) {
        this.itemId = itemId;
        initialize();
    }

    private void initialize() {
        state = ExecState.SUCCESSFUL;
        nodeStatusMap = new ConcurrentHashMap<>();
        nodeStatusCodeMap = new ConcurrentHashMap<>();
        nodeStatusMessageMap = new ConcurrentHashMap<>();
    }

    @Override
    public void addNodeStatus(String displayId, int statusCode, String message) {
        boolean success = true;
        if(statusCode != 0) {
            state = ExecState.FAILED;
            success = false;
        }

        if(message != null) {
            nodeStatusMessageMap.put(displayId, message);
        }
        nodeStatusMap.put(displayId, success);
        nodeStatusCodeMap.put(displayId, statusCode);
    }

    @Override
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public ExecState getState() {
        return state;
    }

    @Override
    public void setState(ExecState state) {
        this.state = state;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public Map<String, Integer> getNodeStatusCodeMap() {
        return nodeStatusCodeMap;
    }

    public void setNodeStatusCodeMap(Map<String, Integer> nodeStatusCodeMap) {
        this.nodeStatusCodeMap = nodeStatusCodeMap;
    }

    @Override
    public Map<String, Boolean> getNodeStatusMap() {
        return nodeStatusMap;
    }

    public void setNodeStatusMap(Map<String, Boolean> nodeStatusMap) {
        this.nodeStatusMap = nodeStatusMap;
    }

    @Override
    public Map<String, String> getNodeStatusMessageMap() {
        return nodeStatusMessageMap;
    }

    public void setNodeStatusMessageMap(Map<String, String> nodeStatusMessageMap) {
        this.nodeStatusMessageMap = nodeStatusMessageMap;
    }
}
