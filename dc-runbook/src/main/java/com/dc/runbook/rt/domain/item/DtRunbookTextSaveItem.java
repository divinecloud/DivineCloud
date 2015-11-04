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
