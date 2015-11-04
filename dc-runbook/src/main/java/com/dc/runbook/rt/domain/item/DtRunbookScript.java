/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.dc.runbook.rt.domain.item;

import java.util.List;

import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Script")
public class DtRunbookScript extends DtRunbookItem {
    private String script;
    private List<String> args;
    private String fileName;
    private ScriptLanguage language;
    private String invokingProgram;
    
	public DtRunbookScript() {
		this.type = DtRunbookItemType.Script;
    }

	public DtRunbookScript(int runbookId, int itemId, String script, List<String> args, List<String> answers, ScriptLanguage language, String invokingProgram, String fileName, boolean reboot) {
        super(runbookId, itemId, answers, reboot);
        this.script = script;
        this.args = args;
        this.language = language;
        this.invokingProgram = invokingProgram;
        this.fileName = fileName;
		this.type = DtRunbookItemType.Script;
    }

	public void setScript(String script) {
		this.script = script;
	}

	public void setArgs(List<String> arguments) {
		this.args = arguments;
	}

    public String getScript() {
        return script;
    }

    public List<String> getArgs() {
        return args;
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

}
