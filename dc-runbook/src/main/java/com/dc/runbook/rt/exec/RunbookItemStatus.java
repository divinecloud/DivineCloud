/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
