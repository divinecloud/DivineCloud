package com.dc.runbook.rt.exec.support;

public class NodeInfo {
    private String uniqueId;
    private String dynamicTag;
    private boolean onDemand;

    public NodeInfo(String uniqueId, String dynamicTag, boolean onDemand) {
        this.uniqueId = uniqueId;
        this.dynamicTag = dynamicTag;
        this.onDemand = onDemand;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getDynamicTag() {
        return dynamicTag;
    }

    public boolean isOnDemand() {
        return onDemand;
    }
}
