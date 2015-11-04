package com.dc.runbook.dt.domain.item;

import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("FileScript")
public class FileScriptItem extends RunBookItem {
    private String scriptPath;
    private String arguments;
    private String fileName;
    private ScriptLanguage language;
    private String invokingProgram;
    private boolean relative;
    
	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public boolean isRelative() {
		return relative;
	}

	public void setRelative(boolean relative) {
		this.relative = relative;
	}

	@Override
    public String toString() {
        return "FileScriptItem{" +
                "scriptPath='" + scriptPath + '\'' +
                ", arguments='" + arguments + '\'' +
                ", fileName='" + fileName + '\'' +
                ", language='" + language + '\'' +
                ", invokingProgram='" + invokingProgram + '\'' +
                ", relative='" + relative + '\'' +
                '}';
    }
}
