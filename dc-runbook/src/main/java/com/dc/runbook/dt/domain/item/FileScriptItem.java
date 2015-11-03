/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
