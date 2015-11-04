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

import java.util.List;

import com.dc.LinuxOSType;
import com.dc.support.KeyValuePair;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MultiOsCommand")
public class MultiOsCommandItem extends RunBookItem {
	private List<KeyValuePair<String, LinuxOSType>> commandsList;
	
	public List<KeyValuePair<String, LinuxOSType>> getCommandsList() {
		return commandsList;
	}

	public void setCommandsList(List<KeyValuePair<String, LinuxOSType>> commandsList) {
		this.commandsList = commandsList;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MultiCommandItem{commands=[");
		for(KeyValuePair<String, LinuxOSType> command : commandsList) {
			sb.append(command.toString());
		}
		sb.append("]}");
		return sb.toString();

	}
}
