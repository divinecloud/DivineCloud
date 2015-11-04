package com.dc.runbook.rt.exec;

import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.runbook.rt.domain.NodeOutputChunk;
import com.dc.runbook.rt.domain.item.DtRunbookItem;

public interface RunbookCallback {
	public DtRunbook getRunbook();
	
	public RunbookItemStatus executingItem(DtRunbookItem runbookItem);
	
	public RunbookItemStatus completedItem(DtRunbookItem runbookItem);

    public RunbookItemStatus skippingItem(DtRunbookItem runbookItem);

	public RunbookItemStatus pausedItem(DtRunbookItem runbookItem);

	public RunbookItemStatus resumedItem(DtRunbookItem runbookItem);

	public void output(NodeOutputChunk nodeOutputChunk);

	public void error(NodeOutputChunk nodeOutputChunk);

	public void started();

	public void markCancelled();
	
	public void done();
	
	public void done(Exception e);
	
	public String getExecutionId();
	
	public void itemExecOnNodeDone(String itemId, String nodeId, int statusCode, String message);

    public boolean didLatestStepFail();
}
