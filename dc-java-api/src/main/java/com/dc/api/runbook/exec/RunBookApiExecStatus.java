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

package com.dc.api.runbook.exec;

import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.runbook.rt.domain.NodeSelectionMap;
import com.dc.runbook.rt.domain.TransformedRunBook;
import com.dc.runbook.rt.exec.ExecState;
import com.dc.runbook.rt.exec.RunbookItemStatus;
import com.dc.runbook.rt.exec.RunbookStatus;
import com.dc.runbook.rt.exec.output.StepExecutionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RunBookApiExecStatus implements RunbookStatus {

    private String	                           executionId;
    private Map<String, RunbookItemStatus>	itemStatusMap;
    private long	                           startTime;
    private long	                           endTime;
    private boolean	                           complete;
    private ExecState	                       state;

    private DtRunbook	                       runbook;
    private TransformedRunBook	               transformedRunbook;
    private List<NodeSelectionMap>	           nodesMap;
    private Map<String, Map<String, String>>	outputMap;

    private String errorMessage;

    public RunBookApiExecStatus() {
        itemStatusMap = new ConcurrentHashMap<>();
        nodesMap = new ArrayList<>();
        outputMap = new ConcurrentHashMap<>();

    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public RunbookItemStatus getItemStatus(String itemId) {
        return itemStatusMap.get(itemId);
    }

    public void addItemStatus(String itemId, RunbookItemStatus itemStatus) {
        itemStatusMap.put(itemId, (StepExecutionStatus) itemStatus);
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

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public ExecState getState() {
        return state;
    }

    public void setState(ExecState state) {
        this.state = state;
    }

    public DtRunbook getRunbook() {
        return runbook;
    }

    public void setRunbook(DtRunbook runbook) {
        this.runbook = runbook;
    }

    public Map<String, RunbookItemStatus> getStatusMap() {
        return itemStatusMap;
    }

    public void setStatusMap(Map<String, RunbookItemStatus> statusMap) {
        itemStatusMap = statusMap;
    }

    public TransformedRunBook getTransformedRunbook() {
        return transformedRunbook;
    }

    public void setTransformedRunbook(TransformedRunBook transformedRunbook) {
        this.transformedRunbook = transformedRunbook;
    }

    public List<NodeSelectionMap> getNodesMap() {
        return nodesMap;
    }

    public void setNodesMap(List<NodeSelectionMap> nodesMap) {
        this.nodesMap = nodesMap;
    }

    public Map<String, Map<String, String>> getOutputMap() {
        return outputMap;
    }

    public void setOutputMap(Map<String, Map<String, String>> outputMap) {
        this.outputMap = outputMap;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
