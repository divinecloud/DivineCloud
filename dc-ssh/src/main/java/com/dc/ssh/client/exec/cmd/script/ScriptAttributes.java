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

import com.dc.ssh.client.exec.cmd.SshCommandAttributes;

public class ScriptAttributes {

    private SshCommandAttributes commandAttributes;
    private String arguments;
    private ScriptLanguage language;
    private String invokingProgram;
    private String filePath;

    public ScriptAttributes(SshCommandAttributes commandAttributes, String arguments, ScriptLanguage language, String invokingProgram, String filePath) {
        this.commandAttributes = commandAttributes;
        this.arguments = arguments;
        this.language = language;
        this.invokingProgram = invokingProgram;
        this.filePath = filePath;
    }

    public SshCommandAttributes getCommandAttributes() {
        return commandAttributes;
    }

    public String getArguments() {
        return arguments;
    }

    public ScriptLanguage getLanguage() {
        return language;
    }

    public String getInvokingProgram() {
        return invokingProgram;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return "ScriptAttributes{" +
                "commandAttributes=" + commandAttributes +
                ", arguments='" + arguments + '\'' +
                ", language=" + language +
                ", invokingProgram='" + invokingProgram + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
