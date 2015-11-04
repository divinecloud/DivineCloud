package com.dc.runbook.dt.domain.item;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MultiScriptCommand")
public class MultiScriptCommandItem extends RunBookItem {
	private List<String>	commands;

	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MultiScriptCommandItem{commands=[");
		sb.append(commands.toArray().toString());
		sb.append("]}");
		return sb.toString();

	}
}
