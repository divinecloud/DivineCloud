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

package com.dc.ssh.client.support;

import com.dc.ssh.client.SshConnectException;
import com.dc.ssh.client.SshErrorTokens;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.support.callback.LocalCallback;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler class for dealing with ssh channel related operations.
 */
public class SshChannelHandler {
    private Session session;
    private SshClientConfiguration configuration;
    private LocalCallback callback;
    private Channel channel;
    private OutputStream out;
    private String commandString;
    private String runAsPassword;
    private List<String> answers;

    public SshChannelHandler(Session session, SshClientConfiguration configuration, LocalCallback localCallback, String commandString, String password, List<String> answers) throws SshException {
        this.session = session;
        this.configuration = configuration;
        this.callback = localCallback;
        this.commandString = commandString;
        this.runAsPassword = password;
        this.answers = answers;

        channel = createChannel();
    }

    private Channel createChannel() throws SshConnectException {
        Channel channel;
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setPty(configuration.isPtySupport());
            //((ChannelExec) channel).setPtyType("vt102");
        }
        catch (JSchException e) {
            throw new SshConnectException("Unable to execute command for host : " + session.getHost(), e, true, SshErrorTokens.CHANNEL_CANNOT_OPEN);
        }
        catch(Throwable t) {
            throw new SshConnectException("UNABLE TO EXECUTE COMMAND FOR HOST : " + session.getHost(), t, true, SshErrorTokens.CHANNEL_CANNOT_OPEN);
        }
        return channel;
    }

    public void cancel() throws SshException {
        try {
            //channel.sendSignal("KILL");
            callback.executionCancelled();
            channel.disconnect();
        } catch (Throwable e) {
            throw new SshException("Error occurred while cancelling command execution for Host : " + session.getHost(), e, true, SshErrorTokens.CANCEL_FAILED);
        }

    }

    public void disconnect() throws SshException {
        try {
            channel.disconnect();
        } catch (Throwable e) {
            throw new SshException("Error occurred while cancelling command execution for Host : " + session.getHost(), e, true, SshErrorTokens.GENERIC_EXECUTE_ERROR);
        }
    }

    public void runCommand() throws SshException {
        runCommand(getTimeoutThreshold(configuration), configuration.getTotalConnectAttempts());
    }

    public void runCommand(long timeoutThreshold, int totalConnectAttempts) throws SshException {
        try {
            read(timeoutThreshold, totalConnectAttempts);
        } catch (IOException | JSchException e) {
            throw new SshException("Exception occurred while reading/writing data for Host : " + session.getHost(), e, true, SshErrorTokens.GENERIC_READ_WRITE_ERROR);
        } catch(Throwable t) {
            throw new SshException("Exception occurred while reading/writing data for Host : " + session.getHost(), t, true, SshErrorTokens.GENERIC_EXECUTE_ERROR);
        }
    }
    private void feedAnswers(OutputStream out, List<String> answers) throws IOException {
        if(answers != null && answers.size() > 0) {
            for(String answer: answers) {
                out.write((answer + "\n").getBytes()); //TODO: Later need to check if stream is available before writing
                out.flush();
            }
        }
    }

    public void feedAnswer(String answer) throws SshException {
        try {
            out.write((answer + "\n").getBytes());
            out.flush();
        } catch (IOException e) {
            throw new SshException("Unable to write the answer to the output stream", e);
        }
    }

    private void read(long timeoutThreshold, int totalConnectAttempts) throws IOException, SshException, JSchException {

        try(InputStream in = channel.getInputStream();
            InputStream error = ((ChannelExec) channel).getErrStream()) {

            out = channel.getOutputStream();
            ((ChannelExec) channel).setCommand(commandString);
            connectChannel(channel, configuration, totalConnectAttempts);

            byte[] tmp = new byte[configuration.getBufferSize()];
            byte[] tempErr = new byte[configuration.getBufferSize()];
            long sleepTime = 0;
            boolean done = false;

            if(runAsPassword != null && runAsPassword.trim().length() > 0) {
                ArrayList<String> list = new ArrayList<>();
                list.add(runAsPassword);
                feedAnswers(out, list);
            }
            if(answers != null && answers.size() > 0) {
                try {
                    // This is a workaround for a bug in mysql cmd line, where if empty answer fed too early is being ignored.
                    // Later figure out a better way of ensuring the receiving process is ready for answers.
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    System.out.println("Interrupted while sleeping to ensure the receiving process is ready for receiving answers");
                }
                feedAnswers(out, answers);
            }
            while (!done && sleepTime < timeoutThreshold) {

                if (in.available() > 0) {
                    readCmdExecOutputData(callback, tmp, in);
                    sleepTime = 0;
                }
                if (error.available() > 0) {
                    readCmdExecErrorData(callback, tempErr, error);
                    sleepTime = 0;
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    if (error.available() > 0) continue;
                    done = true;
                } else {
                    sleepTime = sleepConditionally(sleepTime, in, error);
                }
            }
        } finally {
            if(out != null) {
                out.close();
            }
            callback.done(channel.getExitStatus());
        }
    }

    private long sleepConditionally(long sleepTime, InputStream in, InputStream error) throws IOException, SshException {
        try {
            if (!(in.available() > 0) && !(error.available() > 0) && !channel.isClosed()) {
                Thread.sleep(20);
                sleepTime += 20;
            }
        } catch (InterruptedException ee) {
            throw new SshException("SSH Channel Interruption occurred for Host : " + session.getHost(), ee, true, SshErrorTokens.SLEEP_INTERRUPTED);
        }
        return sleepTime;
    }

    private void readCmdExecErrorData(LocalCallback callback, byte[] tempErr, InputStream error) throws IOException {
        int j = error.read(tempErr, 0, configuration.getBufferSize());
        byte[] result = new byte[j];
        System.arraycopy(tempErr, 0, result, 0, j);
        callback.output(result);
    }

    private void readCmdExecOutputData(LocalCallback callback, byte[] tmp, InputStream in) throws IOException {
        int i=in.read(tmp, 0, configuration.getBufferSize());
        byte[] result = new byte[i];
        System.arraycopy(tmp, 0, result, 0, i);
        callback.output(result);
    }

    private long getTimeoutThreshold(SshClientConfiguration configuration) {
        long timeoutThreshold = configuration.getReadTimeout();
        if(timeoutThreshold <= 0) {
            timeoutThreshold = 30000;
        }
        return timeoutThreshold;
    }


    private void connectChannel(Channel channel, SshClientConfiguration configuration, int connectAttempts) throws SshConnectException {
        int connectWaitTime = configuration.getConnectWaitTime();
        int totalConnectAttempts = connectAttempts;
        if(totalConnectAttempts <= 0) {
            totalConnectAttempts = 1;
        }
        for(int i=0; i<totalConnectAttempts; i++) {
            try {
                channel.connect(connectWaitTime);
                break;
            } catch (JSchException e) {
                //Check if all the connect attempts are made as per configuration
                if(totalConnectAttempts == (i + 1)) {
                    //e.printStackTrace();
                    throw new SshConnectException("Unable to connect for Host : " + session.getHost(), e, true, SshErrorTokens.GENERIC_CONNECT_ERROR);
                }
                //If not, continue till total connect attempts reached as per configuration
            } catch (Throwable t) {
                //t.printStackTrace();
                throw new SshConnectException("UNABLE TO CONNECT TO HOST : " + session.getHost(), t, true, SshErrorTokens.GENERIC_CONNECT_ERROR);
            }
        }
    }

}
