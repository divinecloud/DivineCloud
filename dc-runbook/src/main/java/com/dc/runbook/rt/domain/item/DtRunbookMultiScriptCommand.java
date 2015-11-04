package com.dc.runbook.rt.domain.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MultiScriptCommand")
public class DtRunbookMultiScriptCommand extends DtRunbookItem {
	private List<String>	commands;

	public DtRunbookMultiScriptCommand() {
		this.type = DtRunbookItemType.MultiScriptCommand;
	}

	public DtRunbookMultiScriptCommand(int runbookId, int itemId, List<String> commands, List<String> answers, boolean reboot) {
		super(runbookId, itemId, answers, reboot);
		this.commands = commands;
		this.type = DtRunbookItemType.MultiScriptCommand;
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
