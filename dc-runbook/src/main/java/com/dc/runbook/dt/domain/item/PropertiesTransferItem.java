package com.dc.runbook.dt.domain.item;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PropertiesTransfer")
public class PropertiesTransferItem extends RunBookItem {
    private String path;
    

	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	@Override
    public String toString() {
        return "PropertiesTransferItem{" +
                "path='" + path + '\'' +
                '}';
    }
}
