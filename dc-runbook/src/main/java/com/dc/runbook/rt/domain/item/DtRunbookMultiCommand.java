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
