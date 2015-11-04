package com.dc.runbook.rt.exec.output;

import com.dc.runbook.rt.domain.NodeSelectionMap;
import com.dc.runbook.rt.exec.ExecState;

import java.util.List;
import java.util.Map;

public class RunBookOutput {

    private String executionId;
    private long startTime;
    private long	             endTime;
    private boolean	             complete;
    private ExecState status;
    private List<NodeSelectionMap> nodesPerStep;
    private Map<String, Map<String, String>> outputMap;
    private Map<String, StepExecutionStatus> stepExecutionStatusMap;

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
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

    public List<NodeSelectionMap> getNodesPerStep() {
        return nodesPerStep;
    }

    public void setNodesPerStep(List<NodeSelectionMap> nodesPerStep) {
        this.nodesPerStep = nodesPerStep;
    }

    public ExecState getStatus() {
        return status;
    }

    public void setStatus(ExecState status) {
        this.status = status;
    }

    public Map<String, Map<String, String>> getOutputMap() {
        return outputMap;
    }

    public void setOutputMap(Map<String, Map<String, String>> outputMap) {
        this.outputMap = outputMap;
    }

    public Map<String, StepExecutionStatus> getStepExecutionStatusMap() {
        return stepExecutionStatusMap;
    }

    public void setStepExecutionStatusMap(Map<String, StepExecutionStatus> stepExecutionStatusMap) {
        this.stepExecutionStatusMap = stepExecutionStatusMap;
    }
}
