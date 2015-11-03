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

package com.dc.ssh.client.exec.cmd;

/**
 * Single command implementation for SshCommand.
 */
public class SingleSshCommand extends AbstractSshCommand {
    private String command;


    public SingleSshCommand(SshCommandAttributes cmdAttributes, String command) {
        super(cmdAttributes);
        this.command = command;
    }

    public SingleSshCommand(String executionId, String command) {
        super(new SshCommandAttributes(executionId, null, null, false));
        this.command = command;
    }


    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "SingleSshCommand{" +
                "command='" + command + '\'' +
                "} " + super.toString();
    }

    @Override
    public String prettyCode() {
        String result = command;

        if(command != null && command.length() > 64) {
            result = command.substring(0, 64) + " ...";
        }
        return result;
    }
}
