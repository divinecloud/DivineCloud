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

import java.util.List;

/**
 * Interface representing the SSH Command Input.
 */
public interface SshCommand {

    /**
     * Gets the user name to run the command as.
     *
     * @return runAs user name
     */
    public String runAsUser();

    /**
     * Gets the password for the user to run as.
     *
     * @return runAs user password
     */
    public String runAsPassword();

    /**
     * Indicates whether to run this command as admin.
     *
     * @return true, if command is to be run as root
     */
    public boolean runAsAdmin();

    /**
     * Gets the execution id for the executing command.
     * Note: It's responsibility of the calling code to ensure the execution id is unique for the given context.
     *
     * @return execution id
     */
    public String getExecutionId();

    /**
     * All the answers that need to be provided in interactive mode while executing the command.
     *
     * @return list of answers
     */
    public List<String> answers();

    /**
     * Used to know if the given Ssh Command will cause the server to reboot.
     * @return true if reboot will happen when the given command is executed, false otherwise.
     */
    public boolean causeReboot();


    /**
     * Used to provide the starting few characters of the command code for display purpose.
     *
     * @return command string or the subset of the command string, if the command/code is long.
     */
    public String prettyCode();

    /**
     * Returns the timeout Threshold if set.
     *
     * @return the timeout threshold if set.
     */
    public long getTimeoutThreshold();
}
