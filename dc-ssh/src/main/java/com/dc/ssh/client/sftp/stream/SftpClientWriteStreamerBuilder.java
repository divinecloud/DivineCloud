package com.dc.ssh.client.sftp.stream;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.impl.SftpClientWriteStreamerImpl;
import com.dc.ssh.client.exec.vo.NodeCredentials;

public class SftpClientWriteStreamerBuilder {
    public static SftpClientWriteStreamer build(NodeCredentials credentials, String target) throws SftpClientException {
        SshClientConfiguration configuration   = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(40000).build();
        return new SftpClientWriteStreamerImpl(credentials, configuration, target);
    }
}
