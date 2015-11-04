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

package com.dc.ssh.client.exec.support;

import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.cmd.script.BaseScriptCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.support.RunAsAdminSupport;

public class CommandFormatter {

    public static String generateDefaultFileName(ScriptLanguage language, String executionId) {
        String extension;
        switch(language) {
            case Shell:
                extension = ".sh";
                break;
            case Ruby:
                extension = ".rb";
                break;
            case Perl:
                extension = ".pl";
                break;
            case Python:
                extension = ".py";
                break;
            case Groovy:
                extension = ".groovy";
                break;
            default:
                extension = ".txt";

        }
        return executionId + extension;
    }


    public static String formatCommand(SingleSshCommand command) {
        return RunAsAdminSupport.conditionallyAddSuperUser(command.getCommand(), command.runAsUser(), command.runAsAdmin());
    }

    public static String formatCommand(BaseScriptCommand command, String destination) throws SshException {
        String commandString;
        String program = command.getInvokingProgram();
        if("bash".equals(program)) {
            program = "bash -e";
        }
        if(command.getArguments() != null) {
            commandString = program + " " + destination + " " + command.getArguments();
        }
        else {
            commandString = program + " " + destination;
        }

        return RunAsAdminSupport.conditionallyAddSuperUser(commandString, command.runAsUser(), command.runAsAdmin());
    }
}
