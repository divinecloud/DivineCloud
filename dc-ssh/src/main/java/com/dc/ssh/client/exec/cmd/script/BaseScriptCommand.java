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

package com.dc.ssh.client.exec.cmd.script;


import com.dc.ssh.client.exec.cmd.AbstractSshCommand;

/**
 * Marker class for all the script commands.
 */
public abstract class BaseScriptCommand extends AbstractSshCommand {
    private ScriptAttributes scriptAttributes;

    public BaseScriptCommand(ScriptAttributes scriptAttributes) {
        super(scriptAttributes.getCommandAttributes());
        this.scriptAttributes = scriptAttributes;
    }

    public abstract String getCode();

    public String getArguments() {
        return scriptAttributes.getArguments();
    }

    public ScriptLanguage getLanguage() {
        return scriptAttributes.getLanguage();
    }

    public String getInvokingProgram() {
        return scriptAttributes.getInvokingProgram();
    }

    public String getFilePath() {
        return scriptAttributes.getFilePath();
    }

    @Override
    public String toString() {
        return "ScriptCommand{" +
                "scriptAttributes=" + scriptAttributes +
                '}';
    }
}
