package com.dc.support;

import java.util.Map;

public class ExecutionOutput {
    private Map<String, String> outputMap;
    private Map<String, Integer> statusCodeMap;

    public Map<String, String> getOutputMap() {
        return outputMap;
    }

    public void setOutputMap(Map<String, String> outputMap) {
        this.outputMap = outputMap;
    }

    public Map<String, Integer> getStatusCodeMap() {
        return statusCodeMap;
    }

    public void setStatusCodeMap(Map<String, Integer> statusCodeMap) {
        this.statusCodeMap = statusCodeMap;
    }
}
