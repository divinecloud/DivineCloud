/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
