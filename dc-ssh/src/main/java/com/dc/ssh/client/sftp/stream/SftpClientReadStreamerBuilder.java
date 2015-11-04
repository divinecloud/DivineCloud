package com.dc.ssh.client.sftp.stream;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.impl.SftpClientReadStreamerImpl;
import com.dc.ssh.client.exec.vo.NodeCredentials;

public class SftpClientReadStreamerBuilder {

    public static SftpClientReadStreamer build(NodeCredentials credentials, String target) throws SftpClientException {
        SshClientConfiguration configuration   = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(40000).build();

        return new SftpClientReadStreamerImpl(credentials, configuration, target);
    }
}
