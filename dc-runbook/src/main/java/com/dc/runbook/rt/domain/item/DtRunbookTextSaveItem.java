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

import com.dc.runbook.dt.domain.item.TextSaveMode;

public class DtRunbookTextSaveItem extends DtRunbookItem {
	private String	     fileName;
	private TextSaveMode	mode;
	private boolean	     backup;
	private String	     text;

	public DtRunbookTextSaveItem() {
		super();
		this.type = DtRunbookItemType.TextSave;
	}

	public DtRunbookTextSaveItem(int runbookId, int itemId, List<String> answers, boolean reboot, String fileName, String text, TextSaveMode mode, boolean backup) {
		super(runbookId, itemId, answers, reboot);
		this.type = DtRunbookItemType.TextSave;
		this.fileName = fileName;
		this.backup = backup;
		this.mode = mode;
		this.text = text;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public TextSaveMode getMode() {
		return mode;
	}

	public void setMode(TextSaveMode mode) {
		this.mode = mode;
	}

	public boolean isBackup() {
		return backup;
	}

	public void setBackup(boolean backup) {
		this.backup = backup;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
