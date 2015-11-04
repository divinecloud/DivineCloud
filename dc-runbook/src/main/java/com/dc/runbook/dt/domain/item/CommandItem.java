package com.dc.runbook.dt.domain.item;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Command")
public class CommandItem extends RunBookItem {
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "CommandItem{" +
                "command='" + command + '\'' +
                '}';
    }
}
