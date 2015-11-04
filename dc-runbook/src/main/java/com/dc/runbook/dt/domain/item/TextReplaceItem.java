package com.dc.runbook.dt.domain.item;

import java.util.List;

import com.dc.support.KeyValuePair;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TextReplace")
public class TextReplaceItem extends RunBookItem {
    private String fileName;
    private boolean backup;
    private List<KeyValuePair<String, String>> propertiesList;


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


	public List<KeyValuePair<String, String>> getPropertiesList() {
		return propertiesList;
	}


	public void setPropertiesList(List<KeyValuePair<String, String>> propertiesList) {
		this.propertiesList = propertiesList;
	}

	@Override
    public String toString() {
        return "TextReplaceItem{" +
                "fileName='" + fileName + '\'' +
                ", backup='" + backup + '\'' +
                '}';
    }
}
