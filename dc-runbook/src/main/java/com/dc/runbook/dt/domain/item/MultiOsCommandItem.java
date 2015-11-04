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
