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

package com.dc.ssh.client.exec;

import com.dc.ssh.batch.sftp.SftpMode;
import com.dc.ssh.client.CommandExecutionCallback;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.cmd.SshCommand;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.sftp.SftpClient;
import com.dc.ssh.client.sftp.stream.SftpStreamer;
import com.dc.support.KeyValuePair;

import java.util.List;

/**
 * Interface representation for Ssh client.
 */
public interface SshClient extends AutoCloseable {

    /**
     * Unique ID for the given SshClient.
     *
     * @return ssh client id.
     */
    public String id();

    /**
     * Host IP for the given SshClient.
     *
     * @return ssh client ip.
     */
    public String ip();

    /**
     * Checks if the client is still connected.
     *
     * @return true if connected.
     */
    public boolean isConnected();

    /**
     * Checks if client is connected but idle for more than configured amount of time.
     *
     * @return true if idle for long time.
     */
    public boolean isIdle();

    /**
     * Returns the last activity time in milliseconds.
     *
     * @return last activity time in milliseconds.
     */
    public long lastActivityAt();

    /**
     * Returns the Sftp Client instance for transferring file across the wire.
     *
     * @return Sftp client implementation.
     */
    public SftpClient getSftpClient();

    public SftpStreamer getSftpStreamer(SftpMode mode, String path);


    /**
     * Pings the server to verify if its still connected.
     * @return true if still connected.
     */
    public boolean ping();

    /**
     * Pings the server to verify if its still connected.
     *
     * @param millis - no. of milliseconds to wait before giving up and returning false.
     * @return true if still connected
     */
    public boolean ping(long millis);

    /**
     * Executes the specified command on the given host. The output and error is streamed back via CommandExecutionCallback instance.
     *
     * @param sshCommand - SSH command to be ran on the given host
     * @param callback - command execution callback
     *
     * @return the Command Execution Id
     * @throws SshException - Gets thrown for any SSH related issues.
     */
    public String execute(SshCommand sshCommand, CommandExecutionCallback callback) throws SshException;

    /**
     * Executes the specified command on the given host. This is blocking command, and will return only after command execution is complete.
     *
     * @param sshCommand - SSH command to be ran on the given host
     *
     * @return the Command Execution details
     * @throws SshException - Gets thrown for any SSH related issues.
     */
    public ExecutionDetails execute(SingleSshCommand sshCommand) throws SshException;

    /**
     * Executes the specified command string on the given host. This is blocking command, and will return only after command execution is complete.
     * @param command - SSH command string to be ran on the given host
     *
     * @return the Command Execution details
     * @throws SshException - Gets thrown for any SSH related issues.
     */
    public ExecutionDetails execute(String command) throws SshException;

    /**
     * Executes the specified command string on the given host. This is blocking command, and will return only after command execution is complete.
     * @param command - SSH command string to be ran on the given host
     * @param timeoutThreshold - Timeout threshold in milliseconds for command execution
     *
     * @return the Command Execution details
     * @throws SshException - Gets thrown for any SSH related issues.
     */
    public ExecutionDetails execute(String command, long timeoutThreshold) throws SshException;


    /**
     * Transfers the files in the list.
     *
     * @param filesList - Files to be transferred.
     *
     * @throws SshException - Gets thrown for any SSH related issues.
     */
    public void transferFiles(List<KeyValuePair<String, byte[]>> filesList) throws SshException;

    /**
     * Feeds the answer to the executing command at runtime.
     *
     * @param executionId - execution id for the running command
     * @param answer - answer to be fed
     *
     * @throws SshException - Gets thrown for any SSH related issues.
     */
    public void feedAnswer(String executionId, String answer) throws SshException;

    /**
     * Cancels the executing ssh command.
     *
     * @param commandExecutionId - ID of the executing command
     *
     * @return true if the command was running and got successfully cancelled. false if not command for given id was running.
     * @throws SshException - Gets thrown for any SSH related issues while cancelling the command execution.
     */
    public boolean cancel(String commandExecutionId) throws SshException;

    /**
     * Closes the SSH connection to the remote host.
     *
     * @throws SshException - Gets thrown for any SSH related issues.
     */
    public void close() throws SshException;
}
