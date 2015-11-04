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

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PropertiesTransfer")
public class DtRunbookPropertiesTransfer extends DtRunbookItem {
	private String	path;

	public DtRunbookPropertiesTransfer() {
		this.type = DtRunbookItemType.PropertiesTransfer;
	}

	public DtRunbookPropertiesTransfer(int runbookId, int itemId, String path) {
		super(runbookId, itemId, null, false);
		this.path = path;
		this.type = DtRunbookItemType.PropertiesTransfer;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
