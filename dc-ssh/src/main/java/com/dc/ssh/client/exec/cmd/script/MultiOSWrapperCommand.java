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
import com.dc.ssh.client.exec.cmd.RunAsAttributes;
import com.dc.ssh.client.exec.cmd.SshCommandAttributes;
import com.dc.support.KeyValuePair;

import java.util.List;

public class MultiOSWrapperCommand extends BaseScriptCommand {
    private LinuxOSType currentLinuxOSType;
    private List<KeyValuePair<String, LinuxOSType>> commandsList;

    public MultiOSWrapperCommand(String executionId, List<KeyValuePair<String, LinuxOSType>> commandsList, LinuxOSType currentType) {
        super(new ScriptAttributes(new SshCommandAttributes(executionId), null, ScriptLanguage.Shell, "bash", null));
        this.commandsList = commandsList;
        this.currentLinuxOSType = currentType;
    }

    public MultiOSWrapperCommand(ScriptAttributes scriptAttributes, List<KeyValuePair<String, LinuxOSType>> commandsList, LinuxOSType currentType) {
        super(scriptAttributes);
        this.commandsList = commandsList;
        this.currentLinuxOSType = currentType;
    }

    public static MultiOSWrapperCommand copy(MultiOSCommand command, LinuxOSType currentType) {
        RunAsAttributes runAsAttributes = null;
        if(command.runAsAdmin() == true || command.runAsUser() != null) {
            runAsAttributes = new RunAsAttributes(command.runAsUser(), command.runAsPassword(), command.runAsAdmin());
        }
        ScriptAttributes attributes = new ScriptAttributes(new SshCommandAttributes(command.getExecutionId(), runAsAttributes, command.answers(), command.causeReboot()), command.getArguments(), command.getLanguage(), command.getInvokingProgram(), command.getFilePath());
        return new MultiOSWrapperCommand(attributes, command.getCommandsList(), currentType);
    }

    public LinuxOSType getCurrentLinuxOSType() {
        return currentLinuxOSType;
    }

    public List<KeyValuePair<String, LinuxOSType>> getCommandsList() {
        return commandsList;
    }

    public String getCode() {
        String result = "";
        for(KeyValuePair<String, LinuxOSType> pair : commandsList) {
            if(pair.getValue() == currentLinuxOSType) {
                result = pair.getKey();
                break;
            }
        }
        return result;
    }

    @Override
    public String prettyCode() {
        String result = "";
        for(KeyValuePair<String, LinuxOSType> pair : commandsList) {
            if(pair.getValue() == currentLinuxOSType) {
                result = pair.getKey();
                break;
            }
        }
        if(result.length() > 64) {
            result = result.substring(0, 64) + " ...";
        }
        return result;
    }

}
