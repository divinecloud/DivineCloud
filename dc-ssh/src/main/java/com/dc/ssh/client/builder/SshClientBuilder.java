package com.dc.ssh.client.builder;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.SshClientImpl;
import com.dc.ssh.client.exec.vo.NodeCredentials;

/**
 * Builder for creating Ssh client builder.
 */
public class SshClientBuilder {

    public static SshClient build(NodeCredentials nodeCredentials, SshClientConfiguration configuration) throws SshException {
        long startTime = System.currentTimeMillis();
        SshClient client = new SshClientImpl(nodeCredentials, configuration);
        long endTime = System.currentTimeMillis();
        System.out.println("Total Connect Time : " + (endTime - startTime));
        return client;
    }
}
