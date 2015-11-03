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

package com.dc.ssh.client.sftp.stream.impl;

import com.dc.ssh.batch.sftp.SftpMode;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.SftpStreamer;
import com.dc.ssh.client.support.SshSessionCreator;
import com.jcraft.jsch.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class SftpStreamerImpl implements SftpStreamer {
    private SftpMode mode;
    private Session session;
    private ChannelSftp sftpChannel;
    private String path;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private String id;
    private boolean localSession;

    public SftpStreamerImpl(SftpMode mode, String id, Session session, String source) {
        this.mode = mode;
        this.id = id;
        this.session = session;
        this.path = source;
        try {
            initialize();
        } catch (SftpException | JSchException e) {
            throw new SftpClientException(e);
        }
    }

    public SftpStreamerImpl(SftpMode mode, String id, NodeCredentials credentials, SshClientConfiguration configuration, String source) {
        this.mode = mode;
        this.id = id;
        this.path = source;
        try {
            session = SshSessionCreator.getInstance().createSession(credentials, configuration);
            localSession = true;
            initialize();
        } catch (SftpException | JSchException e) {
            throw new SftpClientException(e);
        }
    }

    @Override
    public String id() {
        return id;
    }

    private void initialize() throws SftpException, JSchException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        if(mode == SftpMode.DOWNLOAD) {
            in = new BufferedInputStream(sftpChannel.get(path));
        }
        else {
            out = new BufferedOutputStream(sftpChannel.put(path));
        }
    }

    @Override
    public SftpMode mode() {
        return mode;
    }

    @Override
    public void write(byte[] fileContent) throws SftpClientException {
        if(mode != SftpMode.UPLOAD) {
            throw new SftpClientException("File Transfer Mode is set to Download NOT Upload");
        }
        try {
            out.write(fileContent, 0, fileContent.length);
            out.flush();
        } catch (IOException e) {
            throw new SftpClientException(e);
        }

    }

    @Override
    public int read(byte[] buffer) throws SftpClientException {
        if(mode != SftpMode.DOWNLOAD) {
            throw new SftpClientException("File Transfer Mode is set to Upload NOT Download");
        }

        int result;
        try {
            result = in.read(buffer);
        } catch (IOException e) {
            throw new SftpClientException(e);
        }
        return result;
    }

    @Override
    public void close() throws SftpClientException {
        if(mode == SftpMode.DOWNLOAD) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new SshException("IOException occurred while closing InputStream.", e);
                } finally {
                    sftpChannel.disconnect();
                    if(localSession) {
                        session.disconnect();
                    }
                }
            }
        }
        else {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new SshException("IOException occurred while closing OutputStream.", e);
                }
                finally {
                    sftpChannel.disconnect();
                    if(localSession) {
                        session.disconnect();
                    }
                }
            }
        }
    }

}
