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

package com.dc.runbook.rt.domain.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@Type(value = DtRunbookCommand.class, name = "Command"), 
	@Type(value = DtRunbookScript.class, name = "Script"), 
	@Type(value = DtRunbookFileScript.class, name = "FileScript"), 
	@Type(value = DtRunbookFile.class, name = "File"), 
	@Type(value = DtRunbookPropertiesTransfer.class, name = "PropertiesTransfer"), 
	@Type(value = DtRunbookMultiCommand.class, name = "MultiCommand"),
    @Type(value = DtRunbookMultiOsCommand.class, name = "MultiOsCommand"), 
    @Type(value = DtRunbookMultiScriptCommand.class, name = "MultiScriptCommand"), 
    @Type(value = DtRunbookStepGroupItem.class, name = "StepGroup"),
    @Type(value = DtRunbookTextSaveItem.class, name = "TextSave"),
    @Type(value = DtRunbookTextReplaceItem.class, name = "TextReplace")
})

public abstract class DtRunbookItem {
	protected int	            runbookId;
	protected int	            id;
	protected String	        itemId;
	protected List<String>	    answers;
	protected DtRunbookItemType	type;
	protected boolean reboot;
	
	public DtRunbookItem() {
	}

	public DtRunbookItem(int runbookId, int id, List<String> answers, boolean reboot) {
		this.runbookId = runbookId;
		this.id = id;
		this.answers = answers;
		this.reboot = reboot;
		itemId = runbookId + "_" + id;
	}

	public int getRunbookId() {
		return runbookId;
	}

	public void setRunbookId(int runbookId) {
		this.runbookId = runbookId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemId() {
		if (itemId == null) {
			itemId = runbookId + "_" + id;
		}
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public DtRunbookItemType getType() {
		return type;
	}

	public void setType(DtRunbookItemType type) {
		this.type = type;
	}

	public boolean isReboot() {
		return reboot;
	}

	public void setReboot(boolean reboot) {
		this.reboot = reboot;
	}

}
