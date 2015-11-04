package com.dc.runbook.rt.domain.item;

import com.dc.support.KeyValuePair;

import java.util.List;

public class DtRunbookTextReplaceItem extends DtRunbookItem {
    private String fileName;
    private List<KeyValuePair<String, String>> propertiesList;
    private boolean backup;

    
    
    public DtRunbookTextReplaceItem() {
    	super();
    	this.type = DtRunbookItemType.TextReplace;
    }

	public DtRunbookTextReplaceItem(int runbookId, int itemId, List<String> answers, boolean reboot, String fileName, List<KeyValuePair<String, String>> propertiesList, boolean backup) {
		super(runbookId, itemId, answers, reboot);
    	this.type = DtRunbookItemType.TextReplace;
	    this.fileName = fileName;
	    this.propertiesList = propertiesList;
	    this.backup = backup;
    }

	public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<KeyValuePair<String, String>> getPropertiesList() {
        return propertiesList;
    }

    public void setPropertiesList(List<KeyValuePair<String, String>> propertiesList) {
        this.propertiesList = propertiesList;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }
}
