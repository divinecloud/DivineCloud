package com.dc.runbook.rt.domain.item;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PropertiesTransfer")
public class DtRunbookPropertiesTransfer extends DtRunbookItem {
	private String	path;

	public DtRunbookPropertiesTransfer() {
		this.type = DtRunbookItemType.PropertiesTransfer;
	}

	public DtRunbookPropertiesTransfer(int runbookId, int itemId, String path) {
		super(runbookId, itemId, null, false);
		this.path = path;
		this.type = DtRunbookItemType.PropertiesTransfer;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
