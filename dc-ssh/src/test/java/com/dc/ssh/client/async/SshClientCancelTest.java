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

package com.dc.ssh.client.async;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.SampleCallback;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.junit.Assert.*;

/**
 * Unit test for command execution cancellation.
 */
public class SshClientCancelTest {

    @Test
    public void testSingleCmdExecCancel() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "sleep 60000; whoami; echo $PATH");
            SampleCallback callback = new SampleCallback();
            String cmdExecId = sshClient.execute(command, callback);
            CommandCancelInitiator tracker = new CommandCancelInitiator(callback, sshClient, cmdExecId);
            tracker.start();
            tracker.join();
            String result = new String(callback.getOutput());
            assertNotNull(result);
            System.out.println(new String(callback.getOutput()));
            //assertEquals(137, callback.getStatusCode());
            assertFalse(result.contains("Hello SSH World!"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testSingleCmdExecCancelTake2() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "while [ 0 ]; do date;sleep 3;done");
            SampleCallback callback = new SampleCallback();
            String cmdExecId = sshClient.execute(command, callback);
            CommandCancelInitiator tracker = new CommandCancelInitiator(callback, sshClient, cmdExecId);
            tracker.start();
            tracker.join();
            String result = new String(callback.getOutput());
            assertNotNull(result);
            System.out.println(new String(callback.getOutput()).length());
            //assertEquals(137, callback.getStatusCode());

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testSingleCmdExecCancelTake3() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "yes");
            SampleCallback callback = new SampleCallback();
            String cmdExecId = sshClient.execute(command, callback);
            CommandCancelInitiator tracker = new CommandCancelInitiator(callback, sshClient, cmdExecId, 2000);
            tracker.start();
            tracker.join();
            String result = new String(callback.getOutput());
            assertNotNull(result);
            assertTrue(result.length() > 1000);
            System.out.println("Output Size : " + result.length());
            //assertEquals(137, callback.getStatusCode());

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    class CommandCancelInitiator extends Thread {

        private SampleCallback callback;
        private SshClient sshClient;
        private String cmdExecId;
        private long timeInMillis = 8000;
        CommandCancelInitiator(SampleCallback callback, SshClient sshClient, String cmdExecId) {
            this.callback = callback;
            this.sshClient = sshClient;
            this.cmdExecId = cmdExecId;
        }

        CommandCancelInitiator(SampleCallback callback, SshClient sshClient, String cmdExecId, long timeInMillis) {
            this.callback = callback;
            this.sshClient = sshClient;
            this.cmdExecId = cmdExecId;
            this.timeInMillis = timeInMillis;
        }
        public void run() {
            try {
                Thread.sleep(timeInMillis);
                System.out.println("cancelled : " + sshClient.cancel(cmdExecId));
                System.out.println("Command execution cancelled");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SshException e) {
                e.printStackTrace();
            }
        }
    }

}

