package com.dc.runbook.rt.domain;

public class NodeOutputChunk {
	private String	   runbookItemId;
	private String	displayId;
	private String	outputChunk;

	public NodeOutputChunk(String displayId, String outputChunk, String runbookItemId) {
		this.outputChunk = outputChunk;
		this.displayId = displayId;
		this.runbookItemId = runbookItemId;
    }

	public String getOutputChunk() {
		return outputChunk;
	}

	public String getDisplayId() {
		return displayId;
	}

	public String getRunbookItemId() {
		return runbookItemId;
	}

}
