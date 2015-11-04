package com.dc.ssh.client.sftp.stream.impl;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.SftpClientReadStreamer;
import com.dc.ssh.client.support.SshSessionCreator;
import com.jcraft.jsch.*;

import java.io.BufferedInputStream;
import java.io.IOException;

public class SftpClientReadStreamerImpl implements SftpClientReadStreamer {

    private Session session;
    private String source;
    private BufferedInputStream in;

    public SftpClientReadStreamerImpl(Session session, String source) {
        this.session = session;
        this.source = source;
        try {
            initialize();
        } catch (SftpException | JSchException e) {
            throw new SftpClientException(e);
        }
    }

    public SftpClientReadStreamerImpl(NodeCredentials credentials, SshClientConfiguration configuration, String source) {
        this.source = source;
        try {
            session = SshSessionCreator.getInstance().createSession(credentials, configuration);
            initialize();
        } catch (SftpException | JSchException e) {
            throw new SftpClientException(e);
        }
    }

    private void initialize() throws SftpException, JSchException {
        ChannelSftp sftpChannel;
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        in = new BufferedInputStream(sftpChannel.get(source));
    }

    @Override
    public void transfer(byte[] fileContent) throws SftpClientException {
        read(fileContent);
    }

    @Override
    public int read(byte[] buffer) throws SftpClientException {
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
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                throw new SshException("IOException occurred while closing InputStream.", e);
            }
            finally {
                session.disconnect();
            }
        }
    }
}
