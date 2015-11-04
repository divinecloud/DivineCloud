package com.dc.runbook.rt.domain.item;

import java.util.List;

import com.dc.LinuxOSType;
import com.dc.support.KeyValuePair;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MultiOsCommand")
public class DtRunbookMultiOsCommand extends DtRunbookItem {
	private List<KeyValuePair<String, LinuxOSType>> commandsList;

	public DtRunbookMultiOsCommand() {
		this.type = DtRunbookItemType.MultiOsCommand;
	}

	public DtRunbookMultiOsCommand(int runbookId, int itemId, List<KeyValuePair<String, LinuxOSType>> commandsList, List<String> answers, boolean reboot) {
		super(runbookId, itemId, answers, reboot);
		this.commandsList = commandsList;
		this.type = DtRunbookItemType.MultiOsCommand;
	}

	public List<KeyValuePair<String, LinuxOSType>> getCommandsList() {
		return commandsList;
	}

	public void setCommandsList(List<KeyValuePair<String, LinuxOSType>> commandsList) {
		this.commandsList = commandsList;
	}

}
