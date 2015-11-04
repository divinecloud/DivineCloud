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

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MultiCommand")
public class DtRunbookMultiCommand extends DtRunbookItem {
	private List<String>	commands;

	public DtRunbookMultiCommand() {
		this.type = DtRunbookItemType.MultiCommand;
	}

	public DtRunbookMultiCommand(int runbookId, int itemId, List<String> commands, List<String> answers, boolean reboot) {
		super(runbookId, itemId, answers, reboot);
		this.commands = commands;
		this.type = DtRunbookItemType.MultiCommand;
	}

	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}
	
	public String commandsAsScript() {
		StringBuilder sb = new StringBuilder();
		for(String cmd: commands) {
			sb.append(cmd).append("\n");
		}
		return sb.toString();
	}

}
