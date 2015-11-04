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

package com.dc.ssh.client.exec;

import com.dc.exec.ExecutionDetails;
import com.dc.ssh.batch.sftp.SftpMode;
import com.dc.ssh.client.*;
import com.dc.ssh.client.exec.cmd.FileTransferCommand;
import com.dc.ssh.client.exec.cmd.MultipleFileTransferCommand;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.cmd.SshCommand;
import com.dc.ssh.client.exec.cmd.script.BaseScriptCommand;
import com.dc.ssh.client.exec.support.CommandFormatter;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.sftp.SftpClient;
import com.dc.ssh.client.sftp.SftpClientImpl;
import com.dc.ssh.client.sftp.stream.SftpStreamer;
import com.dc.ssh.client.sftp.stream.impl.SftpStreamerImpl;
import com.dc.ssh.client.shell.AbstractSshClient;
import com.dc.ssh.client.shell.NodeConfig;
import com.dc.ssh.client.support.FileTransferSupport;
import com.dc.ssh.client.support.SshChannelHandler;
import com.dc.ssh.client.support.ThreadPoolConfiguration;
import com.dc.ssh.client.support.callback.BufferedLocalCallback;
import com.dc.ssh.client.support.callback.DirectLocalCallback;
import com.dc.ssh.client.support.callback.LocalCallback;
import com.dc.support.KeyValuePair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dc.ssh.client.support.SshClientConstants.PATH_SEPARATOR;
import static com.dc.ssh.client.support.SshClientConstants.SSH_SCRIPT_PERMS;

public class SshClientImpl extends AbstractSshClient implements SshClient {
    private long lastActivityInMillis;
    //private LinuxOSType linuxOSType;
    private SftpClient sftpClient;
    private Map<String, SshChannelHandler> channelHandlerMap;
    private ThreadPoolExecutor threadPoolExecutor;
    private ExecutorCompletionService<String> executorCompletionService;
    private FutureTaskCleanerThread futureTaskCleanerThread;
    private volatile boolean done;
    private volatile boolean rebooting;
    private String temporaryFolder;
    private AtomicInteger counter = new AtomicInteger();
    private int internalId;


    public SshClientImpl(NodeCredentials credentials, SshClientConfiguration configuration) throws SshException {
        super(new NodeConfig(credentials), configuration);
        initialize();
    }

    public SshClientImpl(List<NodeConfig> nodeConfigList, SshClientConfiguration configuration) {
        super(nodeConfigList, configuration);
        initialize();

    }


    private void initialize() {
        temporaryFolder = addBackSlashIfNeeded(configuration.getCachedFilesPath()) + "DivineTerminal" + PATH_SEPARATOR + configuration.getUniqueId();
        internalId = counter.incrementAndGet();
        channelHandlerMap = new ConcurrentHashMap<>();
        sftpClient = new SftpClientImpl(currentSession);
        updateActivityTime();
        LinkedBlockingQueue<Runnable> executorServiceQueue = new LinkedBlockingQueue<>();

        ThreadPoolConfiguration threadPool = configuration.getThreadPoolConfiguration();
        threadPoolExecutor = new ThreadPoolExecutor(threadPool.getMinThreads(), threadPool.getMaxThreads(),
                threadPool.getKeepAliveTime(), TimeUnit.SECONDS, executorServiceQueue);

        executorCompletionService = new ExecutorCompletionService<>(threadPoolExecutor);
        initializeFutureTaskCleanerThread();
        createDirectory(temporaryFolder);
        //discoverLinuxOSType();
    }

    private String addBackSlashIfNeeded(String targetFolder) {
        String result;
        if(targetFolder.equals("/") || targetFolder.endsWith("/")) {
            result = targetFolder;
        }
        else {
            result = targetFolder + PATH_SEPARATOR;
        }
        return result;
    }


    private void initializeFutureTaskCleanerThread() {
        futureTaskCleanerThread = new FutureTaskCleanerThread();
        futureTaskCleanerThread.start();
    }

    @Override
    public void close() throws SshException {
        done = true;
        threadPoolExecutor.shutdown();
        futureTaskCleanerThread.interrupt();
        super.close();
    }

    @Override
    public String execute(SshCommand sshCommand, CommandExecutionCallback callback) throws SshException {
        //System.out.println("ID : " + internalId + " Start Time : " + System.currentTimeMillis());
        setRebootIfRequired(sshCommand);
        if(sshCommand == null || callback == null) {
            throw new SshException("Invalid Input arguments while executing command " +  (sshCommand != null ? sshCommand.prettyCode() : "") + " for Host : "
                    + (currentSession != null ? currentSession.getHost() : ""), null, true, SshErrorTokens.INVALID_ARGS);
        }
        reconnectSessionIfNeeded();

        executorCompletionService.submit(new ResponseCallable(sshCommand, callback));
        //System.out.println("ID : " + internalId + " End Time : " + System.currentTimeMillis());

        return sshCommand.getExecutionId();
    }

    @Override
    public ExecutionDetails execute(SingleSshCommand command) throws SshException {
        return runCommand(command, CommandFormatter.formatCommand(command));
    }

    @Override
    public ExecutionDetails execute(String commandString) throws SshException {
        SingleSshCommand command = new SingleSshCommand(configuration.getUniqueId() + "_cmd_exec_" + counter.incrementAndGet(), commandString);
        return execute(command);
    }

    @Override
    public ExecutionDetails execute(String commandString, long timeoutThreshold) throws SshException {
        SingleSshCommand command = new SingleSshCommand(configuration.getUniqueId() + "_cmd_exec_" + counter.incrementAndGet(), commandString);
        command.setTimeoutThreshold(timeoutThreshold);
        return execute(command);
    }

    public void transferFiles(List<KeyValuePair<String, byte[]>> filesList) {
        try {
            if (filesList != null) {
                for (KeyValuePair<String, byte[]> pair : filesList) {
                    FileTransferSupport.transfer(currentSession, pair.getValue(), pair.getKey(), SSH_SCRIPT_PERMS);
                }
            }
        }
        catch(Throwable t) {
            t.printStackTrace(); //@TODO: Provide more details about failure to the UI layer
            throw new SshException("Unable to transfer file. " + t.getMessage(), t);
        }
    }

    private void setRebootIfRequired(SshCommand sshCommand) {
        if(sshCommand.causeReboot()) {
            rebooting = true;
            try {
                Thread.sleep(5000); //Add sleep to ensure the reboot command effect is visible. Later make the sleep value configurable
            } catch (InterruptedException e) {
                //Ignore exception for this scenario
            }
        }
    }

    private void reconnectSessionIfNeeded() throws SshConnectException {
        if(!currentSession.isConnected()) {
            synchronized(this) {
                if(rebooting) {
                    connectSession(18, 10000); //@TODO: Later this values should be from configuration not hard-coded
                }
                if(!currentSession.isConnected()) {
                    //make N attempts before giving up
                    connectSession(configuration.getTotalConnectAttempts(), configuration.getConnectWaitTime());
                }
                else {
                    rebooting = false;
                }
            }
        }
    }

    private void connectSession(int totalConnectAttempts, long waitTime) {
        int attempt = 0;
        while(attempt < totalConnectAttempts) {
            attempt++;
            //System.out.println("SESSION RE-CONNECT ATTEMPT # " + attempt);
            try {
                createSession();
                break;
            }
            catch(SshConnectException e) {
                if(attempt == totalConnectAttempts) {
                    throw e;
                }
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e1) {
                    throw new SshConnectException("SSH Channel Interruption occurred for Host : " + (currentSession != null ? currentSession.getHost() : ""), e1, true, SshErrorTokens.SLEEP_INTERRUPTED);
                }
            }
        }

    }


    /*
    protected LinuxOSType getLinuxOSType() {
        NodeConfig config = nodeConfigList.get(nodeConfigList.size() - 1);
        NodeCredentials credentials = config.getNodeCredentials();
        LinuxOSType result = credentials.getLinuxOSType();
        if(result == null || result == LinuxOSType.Any) {
            result = (linuxOSType != null) ? linuxOSType : LinuxOSType.Any;
        }
        return result;
    }

    private void discoverLinuxOSType() {
        String execId = configuration.getUniqueId() + "_OS";
        SingleSshCommand command = new SingleSshCommand(execId, "cat /etc/*-release");
        ExecutionDetails execDetails = runCommand(command, CommandFormatter.formatCommand(command));
        SshChannelHandler channelHandler = channelHandlerMap.remove(execId);
        if(channelHandler != null) {
            channelHandler.disconnect();
        }

        linuxOSType = OSTypeParser.parse(new String(execDetails.getOutput()));
    }
    */

    protected void updateActivityTime() {
        lastActivityInMillis = System.currentTimeMillis();
    }

    @Override
    public boolean isConnected() {
        return currentSession.isConnected();
    }

    public boolean isIdle() {
        boolean result = false;
        if(currentSession.isConnected()) {
            if((System.currentTimeMillis() - lastActivityInMillis) > (configuration.getIdleConnectCheckTimeInMinutes() * 60 * 1000)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public long lastActivityAt() {
        return lastActivityInMillis;
    }

    @Override
    public SftpClient getSftpClient() {
        return sftpClient;
    }

    @Override
    public SftpStreamer getSftpStreamer(SftpMode mode, String path) {
        return new SftpStreamerImpl(mode, id(), currentSession, path);
    }

    private void createDirectory(String targetFolder) throws SshException {
        String execId = configuration.getUniqueId() + "_DIR";
        SingleSshCommand folderCreateCommand = new SingleSshCommand(execId, "mkdir -p " + targetFolder);
        ExecutionDetails executionDetails = runCommand(folderCreateCommand, folderCreateCommand.getCommand()); //@TODO: Add timeout logic later on.

        SshChannelHandler channelHandler = channelHandlerMap.remove(execId);
        if(channelHandler != null) {
            channelHandler.disconnect();
        }

        if(executionDetails.isFailed()) {
            throw new SshException("Unable to create directory : " + targetFolder + " for Host : " + (currentSession != null ? currentSession.getHost() : ""), null, true, SshErrorTokens.CANNOT_CREATE_DIR);
        }
        updateActivityTime();
    }

    @Override
    public boolean ping() {
        boolean reachable = false;
        ExecutionDetails details = execute("echo CONNECT_CHECK");

        String output = new String(details.getOutput());

        if (output.contains("CONNECT_CHECK")) {
            reachable = true;
        }
        return reachable;
    }

    @Override
    public boolean ping(long millis) {
        throw new UnsupportedOperationException("ping(long) with timeout not implemented in this version. Instead use ping() without timeout");
    }

    @Override
    public boolean cancel(String commandExecutionId) throws SshException {
        boolean success = false;
        SshChannelHandler channelHandler = channelHandlerMap.get(commandExecutionId);
        if(channelHandler != null) {
            channelHandler.cancel();
            channelHandlerMap.remove(commandExecutionId);
            success = true;
        }
        else {
            System.out.println("Channel Handler should not have been null for command exec id : " + commandExecutionId);
        }
        return success;
    }

    @Override
    public void feedAnswer(String executionId, String answer) throws SshException {
        SshChannelHandler channelHandler = channelHandlerMap.get(executionId);
        if(channelHandler != null) {
            channelHandler.feedAnswer(answer);
        }
    }

    private ExecutionDetails runCommand(SshCommand sshCommand, String commandString) {
        LocalCallback localCallback = new BufferedLocalCallback();
        runCommand(sshCommand, commandString, localCallback);
        updateActivityTime();
        return new ExecutionDetails(localCallback.status(), localCallback.getOutput(), localCallback.getError());
    }

    private void runCommand(SshCommand sshCommand, String commandString, LocalCallback localCallback) {
        System.out.println("Exec ID: " + sshCommand.getExecutionId() + "  command string : " + commandString);
        SshChannelHandler channelHandler = new SshChannelHandler(currentSession, configuration, localCallback, commandString, sshCommand.runAsPassword(), sshCommand.answers());
        channelHandlerMap.put(sshCommand.getExecutionId(), channelHandler);
        if(sshCommand.getTimeoutThreshold() > 0) {
            channelHandler.runCommand(sshCommand.getTimeoutThreshold(), 1);
        }
        else {
            channelHandler.runCommand();
        }
        updateActivityTime();
    }

    private class ResponseCallable implements Callable<String> {
        private SshCommand sshCommand;
        private CommandExecutionCallback callback;

        private ResponseCallable(SshCommand sshCommand, CommandExecutionCallback callback) {
            this.sshCommand = sshCommand;
            this.callback = callback;
        }

        public String call() throws Exception {
            try {
                System.out.println("ID : " + internalId + " Start for exec id : " + sshCommand.getExecutionId() +  " - " + System.currentTimeMillis() + " " + currentSession);
                executeInSeparateThread(sshCommand, callback);
            }
            catch(SshException e) {
                e.printStackTrace();
                callback.done(e);
            }
            catch(Throwable t) {
                t.printStackTrace();
                callback.done(new SshException(t));
            }
            return sshCommand.getExecutionId();
        }

        private void executeInSeparateThread(SshCommand sshCommand, CommandExecutionCallback callback) throws SshException {

            String commandString;
            if(sshCommand instanceof FileTransferCommand) {
                DirectLocalCallback directLocalCallback;
                if(sshCommand.runAsPassword() != null && sshCommand.runAsPassword().trim().length() > 0) {
                    directLocalCallback = new DirectLocalCallback(callback);
                }
                else {
                    directLocalCallback = new DirectLocalCallback(callback);
                }

                FileTransferCommand fileCommand = (FileTransferCommand)sshCommand;
                int status = 0;
                try {
                    FileTransferSupport.transfer(currentSession, fileCommand.getSourceBytes(), fileCommand.getDestination(), SSH_SCRIPT_PERMS);
                }
                catch(Throwable t) {
                    t.printStackTrace(); //@TODO: Provide more details about failure to the UI layer
                    status = 99999;
                }
                finally {
                    directLocalCallback.done(status);
                }

            }
            else if(sshCommand instanceof MultipleFileTransferCommand) {
                DirectLocalCallback directLocalCallback;
                if(sshCommand.runAsPassword() != null && sshCommand.runAsPassword().trim().length() > 0) {
                    directLocalCallback = new DirectLocalCallback(callback);
                }
                else {
                    directLocalCallback = new DirectLocalCallback(callback);
                }
                MultipleFileTransferCommand filesCommand = (MultipleFileTransferCommand)sshCommand;

                List<KeyValuePair<String, byte[]>> filesList = filesCommand.getFilesList();
                int status = 0;
                try {
                    if (filesList != null) {
                        for (KeyValuePair<String, byte[]> pair : filesList) {
                            FileTransferSupport.transfer(currentSession, pair.getValue(), pair.getKey(), SSH_SCRIPT_PERMS);
                        }
                    }
                }
                catch(Throwable t) {
                    t.printStackTrace(); //@TODO: Provide more details about failure to the UI layer
                    status = 99999;
                }
                finally {
                    directLocalCallback.done(status);
                }
            }
            else {
                if(sshCommand instanceof BaseScriptCommand) {
                    //sshCommand = preProcessScriptCommand((BaseScriptCommand)sshCommand);
                    String destination = transferScriptCommand((BaseScriptCommand) sshCommand);
                    commandString = CommandFormatter.formatCommand((BaseScriptCommand) sshCommand, destination);
                }
                else {
                    commandString = CommandFormatter.formatCommand((SingleSshCommand)sshCommand);
                }
                DirectLocalCallback directLocalCallback;
                if(sshCommand.runAsPassword() != null && sshCommand.runAsPassword().trim().length() > 0) {
                    directLocalCallback = new DirectLocalCallback(callback);
                }
                else {
                    directLocalCallback = new DirectLocalCallback(callback);
                }
                runCommand(sshCommand, commandString, directLocalCallback);
            }
        }

        /*
        private BaseScriptCommand preProcessScriptCommand(BaseScriptCommand command) {
            BaseScriptCommand result = command;
            if(command instanceof MultiOSCommand) {
                result = MultiOSWrapperCommand.copy((MultiOSCommand) command, getLinuxOSType());
            }
            return result;
        }
        */

        private String transferScriptCommand(BaseScriptCommand command) {

            String destination;
            byte[] sourceBytes = null;
            String source = command.getCode();
            if(source != null) {
                sourceBytes = source.getBytes();
            }
            if(command.getFilePath() == null || command.getFilePath().trim().length() == 0) {
                destination = temporaryFolder + PATH_SEPARATOR + CommandFormatter.generateDefaultFileName(command.getLanguage(), command.getExecutionId());
            }
            else {
                destination = command.getFilePath();
            }
            FileTransferSupport.transfer(currentSession, sourceBytes, destination, SSH_SCRIPT_PERMS);

            return destination;
        }

    }

    private class FutureTaskCleanerThread extends Thread {
        public void run() {
            while (!done) {
                try {
                    Future<String> execIdFuture = executorCompletionService.take();
                    String execId = execIdFuture.get();
                    if(execId != null) {
                        channelHandlerMap.remove(execId);
                        //System.out.println("ID : " + internalId + " Done with exec id : " + execId +  " - " + System.currentTimeMillis() + " " + currentSession);
                    }
                } catch (InterruptedException e) {
                    if (!done) {
                        e.printStackTrace();
                        break;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    break;
                }
            }
        }
    }
}


