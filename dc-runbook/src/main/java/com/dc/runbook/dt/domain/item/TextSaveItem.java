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
