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

package com.dc.ssh.client.exec.cmd.script;


import com.dc.ssh.client.exec.cmd.SshCommandAttributes;

public class ScriptCommand extends BaseScriptCommand {
    private String code;

    public ScriptCommand(String executionId, String code, ScriptLanguage language, String invokingProgram) {
        super(new ScriptAttributes(new SshCommandAttributes(executionId), null, language, invokingProgram, null));
        this.code = code;
    }

    public ScriptCommand(ScriptAttributes scriptAttributes, String code) {
        super(scriptAttributes);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String prettyCode() {
        String result = code;
        if(code != null && code.length() > 64) {
            result = code.substring(0, 64) + " ...";
        }
        return result;
    }
}
