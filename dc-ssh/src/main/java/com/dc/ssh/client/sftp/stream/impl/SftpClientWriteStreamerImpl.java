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

package com.dc.ssh.client.sftp.stream.impl;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.SftpClientWriteStreamer;
import com.dc.ssh.client.support.SshSessionCreator;
import com.jcraft.jsch.*;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class SftpClientWriteStreamerImpl implements SftpClientWriteStreamer {

    private Session session;
    private String target;
    private BufferedOutputStream out;

    public SftpClientWriteStreamerImpl(Session session, String target) {
        this.session = session;
        this.target = target;
        try {
            initialize();

        } catch (SftpException | JSchException e) {
            throw new SftpClientException("Target Path " + target + " not valid", e); //TODO: add specific error msgs later
        }
    }

    public SftpClientWriteStreamerImpl(NodeCredentials credentials, SshClientConfiguration configuration, String target) {
        this.target = target;
        try {
            session = SshSessionCreator.getInstance().createSession(credentials, configuration);
            initialize();
        } catch (SftpException | JSchException e) {
            throw new SftpClientException("Target Path " + target + " not valid", e); //TODO: add specific error msgs later
        }
    }

    private void initialize() throws SftpException, JSchException {
        long startTime = System.nanoTime();

        ChannelSftp sftpChannel;
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        out = new BufferedOutputStream(sftpChannel.put(target));
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);
    }

    @Override
    public void transfer(byte[] fileContent) throws SftpClientException {
        write(fileContent);
    }

    @Override
    public void write(byte[] fileContent) throws SftpClientException {
        try {
            out.write(fileContent, 0, fileContent.length);
            out.flush();
        } catch (IOException e) {
            throw new SftpClientException(e);
        }
    }

    @Override
    public void close() throws SftpClientException {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                throw new SshException("IOException occurred while closing OutputStream.", e);
            }
            finally {
                session.disconnect();
            }
        }
    }
}
