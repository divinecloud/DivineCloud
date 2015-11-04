package com.dc.runbook.rt.domain.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Command")
public class DtRunbookCommand extends DtRunbookItem {
	private String	command;

	public DtRunbookCommand() {
		this.type = DtRunbookItemType.Command;
	}

	public DtRunbookCommand(int runbookId, int itemId, String command, List<String> answers, boolean reboot) {
		super(runbookId, itemId, answers, reboot);
		this.command = command;
		this.type = DtRunbookItemType.Command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
}
