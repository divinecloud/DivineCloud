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

package com.dc.runbook.dt.domain.item;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TextSave")
public class TextSaveItem extends RunBookItem {
	private String	     fileName;
	private boolean	     backup;
	private String	     text;
	private TextSaveMode	mode;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public TextSaveMode getMode() {
		return mode;
	}

	public void setMode(TextSaveMode mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "TextSaveItem{" + "fileName='" + fileName + '\'' + ", backup='" + backup + '\'' + "mode='" + mode + '\'' + "text='" + text + '\'' + '}';
	}
}
