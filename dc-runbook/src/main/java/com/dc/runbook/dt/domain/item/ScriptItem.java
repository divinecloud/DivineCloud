package com.dc.runbook.dt.domain.item;

import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Script")
public class ScriptItem extends RunBookItem {
    private String script;
    private String arguments;
    private String fileName;
    private ScriptLanguage language;
    private String invokingProgram;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public ScriptLanguage getLanguage() {
		return language;
	}

	public void setLanguage(ScriptLanguage language) {
		this.language = language;
	}

	public String getInvokingProgram() {
		return invokingProgram;
	}

	public void setInvokingProgram(String invokingProgram) {
		this.invokingProgram = invokingProgram;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
    public String toString() {
        return "ScriptItem{" +
                "script='" + script + '\'' +
                ", arguments='" + arguments + '\'' +
                ", fileName='" + fileName + '\'' +
                ", language='" + language + '\'' +
                ", invokingProgram='" + invokingProgram + '\'' +
                '}';
    }
}
