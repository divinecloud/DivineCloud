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
