package com.dc.runbook.rt.exec;

import java.util.Map;

public interface RunbookItemStatus {

	public boolean isComplete();

	public void setComplete(boolean complete);

	public ExecState getState();

	public void setState(ExecState state);

	public long getStartTime();

	public void setStartTime(long startTime);

	public long getEndTime();

	public void setEndTime(long endTime);

	public Map<String, Boolean> getNodeStatusMap();

	public Map<String, Integer> getNodeStatusCodeMap();

	public Map<String, String> getNodeStatusMessageMap();

	public void addNodeStatus(String displayId, int statusCode, String message);

	public void setItemId(String itemId);

	public String getItemId();

}
