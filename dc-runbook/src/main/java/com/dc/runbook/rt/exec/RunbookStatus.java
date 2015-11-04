package com.dc.runbook.rt.exec;

import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.runbook.rt.domain.NodeSelectionMap;
import com.dc.runbook.rt.domain.TransformedRunBook;

import java.util.List;
import java.util.Map;

public interface RunbookStatus {
	public String getExecutionId();

	public void setExecutionId(String executionId);

	public RunbookItemStatus getItemStatus(String itemId);

	public void addItemStatus(String itemId, RunbookItemStatus itemStatus);

	public long getStartTime();

	public void setStartTime(long startTime);

	public long getEndTime();

	public void setEndTime(long endTime);

	public boolean isComplete();

	public void setComplete(boolean complete);

	public ExecState getState();

	public void setState(ExecState state);

	public DtRunbook getRunbook();

	public void setRunbook(DtRunbook runbook);

	public TransformedRunBook getTransformedRunbook();

	public void setTransformedRunbook(TransformedRunBook transformedRunbook);

    public Map<String, RunbookItemStatus> getStatusMap();

    public void setStatusMap(Map<String, RunbookItemStatus> statusMap);

	public List<NodeSelectionMap> getNodesMap();

	public void setNodesMap(List<NodeSelectionMap> nodesMap);

	public Map<String, Map<String, String>> getOutputMap();

	public void setOutputMap(Map<String, Map<String, String>> outputMap);

}
