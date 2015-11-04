package com.dc.runbook.rt.domain.item;

import java.util.List;

import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("FileScript")
public class DtRunbookFileScript extends DtRunbookItem {
	private String	       scriptPath;
	private List<String>	args;
	private String	       fileName;
	private ScriptLanguage	language;
	private String	       invokingProgram;
	private boolean relative;
	
	public DtRunbookFileScript() {
		this.type = DtRunbookItemType.FileScript;
	}

	public DtRunbookFileScript(int runbookId, int itemId, String scriptPath, List<String> args, String fileName, ScriptLanguage language, String invokingProgram, boolean relative, List<String> answers, boolean reboot) {
		super(runbookId, itemId, answers, reboot);
		this.scriptPath = scriptPath;
		this.args = args;
		this.fileName = fileName;
		this.language = language;
		this.invokingProgram = invokingProgram;
		this.relative = relative;
		this.type = DtRunbookItemType.FileScript;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
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

}
