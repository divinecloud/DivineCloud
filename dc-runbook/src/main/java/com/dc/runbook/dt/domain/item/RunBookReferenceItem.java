package com.dc.runbook.dt.domain.item;

import com.dc.runbook.dt.domain.Location;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("RunBook")
public class RunBookReferenceItem extends RunBookItem {
    private String uri;
    private Location location;
    private String version;
    private String checksum;
    private String stepId;
    private boolean utilityMode;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public boolean isUtilityMode() {
        return utilityMode;
    }

    public void setUtilityMode(boolean utilityMode) {
        this.utilityMode = utilityMode;
    }
}
