package com.dc.ssh.client.test.support;

import org.junit.Test;

import com.dc.ssh.client.SshException;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.support.SshSessionCreator;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SampleSshTest {

    @Test
    public void closedSessionChannelConnect() {
        try {
            NodeCredentials credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            Session  session = SshSessionCreator.getInstance().createSession(credentials, configuration);
            session.disconnect();
            Channel channel = session.openChannel("exec");
            System.out.println("channel connect status : " + channel.isConnected());
        } catch (SshException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void invalidSessionChannelConnect() {
        try {
            NodeCredentials credentials = SshTestObjectsGenerator.createInvalidHostNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(5000).build();
            Session  session = SshSessionCreator.getInstance().createSession(credentials, configuration);
            //session.disconnect();
            Channel channel = session.openChannel("exec");
            System.out.println("channel connect status : " + channel.isConnected());
        } catch (SshException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

}
