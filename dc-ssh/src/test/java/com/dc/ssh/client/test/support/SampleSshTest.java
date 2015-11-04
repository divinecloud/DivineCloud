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
