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

import com.dc.LinuxOSType;
import com.dc.ssh.client.exec.cmd.SshCommandAttributes;
import com.dc.support.KeyValuePair;

import java.util.List;

public class MultiOSCommand extends BaseScriptCommand {
    private List<KeyValuePair<String, LinuxOSType>> commandsList;

    public MultiOSCommand(String executionId, List<KeyValuePair<String, LinuxOSType>> commandsList) {
        super(new ScriptAttributes(new SshCommandAttributes(executionId), null, ScriptLanguage.Shell, "bash", null));
        this.commandsList = commandsList;
    }

    public MultiOSCommand(ScriptAttributes scriptAttributes, List<KeyValuePair<String, LinuxOSType>> commandsList) {
        super(scriptAttributes);
        this.commandsList = commandsList;
    }

    public List<KeyValuePair<String, LinuxOSType>> getCommandsList() {
        return commandsList;
    }

    public String getCode() {
        throw new UnsupportedOperationException("This method not supported for this class type");
    }

    @Override
    public String prettyCode() {
        String result = "";
        for(KeyValuePair<String, LinuxOSType> pair : commandsList) {
                result += pair.getKey();
        }
        if(result.length() > 64) {
            result = result.substring(0, 64) + " ...";
        }

        return result;
    }
}
