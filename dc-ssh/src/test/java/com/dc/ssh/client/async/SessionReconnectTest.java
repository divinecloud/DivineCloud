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
import com.dc.ssh.client.exec.cmd.RunAsAttributes;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.cmd.SshCommandAttributes;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.CommandCompletionTracker;
import com.dc.ssh.client.test.support.SampleCallback;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test to verify if the session gets re-connected.
 */
public class SessionReconnectTest {
    private SshClient sshClient;
    @After
    public void after() {
        if(sshClient != null) {
            try {
                sshClient.close();
            } catch (SshException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }
    @Test
    public void testReconnect() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            sshClient = SshClientBuilder.build(credentials, configuration);
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "whoami; sleep 60000; echo $PATH");
            SampleCallback callback = new SampleCallback();
            sshClient.execute(command, callback);
            SessionDisconnectInitiator tracker = new SessionDisconnectInitiator();
            tracker.start();
            tracker.join();
            Thread.sleep(90000);
            SingleSshCommand command2 = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "whoami; echo $PATH");
            SampleCallback callback2 = new SampleCallback();
            sshClient.execute(command2, callback2);
            Thread.sleep(5000);
            assertTrue(callback2.isDone());
            assertEquals(0, callback2.getStatusCode());
            String result = new String(callback2.getOutput());
            assertTrue(result.contains(SshTestObjectsGenerator.getUserName()));
            System.out.println(new String(callback2.getOutput()));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testRebootSupportAsDifferentUser() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            sshClient = SshClientBuilder.build(credentials, configuration);
            RunAsAttributes runAsAttributes = new RunAsAttributes("dcuser", "D1v1n3Cl0ud", false);
            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), runAsAttributes, null, true);

            SingleSshCommand command = new SingleSshCommand(cmdAttributes, "whoami; sleep 60000; echo $PATH");
            SampleCallback callback = new SampleCallback();
            sshClient.execute(command, callback);
            SessionDisconnectInitiator tracker = new SessionDisconnectInitiator();
            tracker.start();
            tracker.join();
            System.out.println(new String(callback.getOutput()));
            System.out.println(new String(callback.getError()));
            assertTrue(new String(callback.getOutput()).contains("dcuser"));

            //Thread.sleep(90000);
            SshCommandAttributes cmd2Attributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), runAsAttributes, null, true);
            SingleSshCommand command2 = new SingleSshCommand(cmd2Attributes, "whoami; echo $PATH");
            SampleCallback callback2 = new SampleCallback();
            System.out.println("Attempting to execute the 2nd command while reboot in progress...");
            sshClient.execute(command2, callback2);
            Thread.sleep(5000);
            assertTrue(callback2.isDone());
            assertEquals(0, callback2.getStatusCode());
            String result = new String(callback2.getOutput());
            assertTrue(result.contains("dcuser"));
            System.out.println(new String(callback2.getOutput()));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testReconnectAtStart() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            sshClient = SshClientBuilder.build(credentials, configuration);
            SessionDisconnectInitiator tracker = new SessionDisconnectInitiator();
            tracker.start();
            tracker.join();
            Thread.sleep(90000);
            SingleSshCommand command2 = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "whoami; echo $PATH");
            SampleCallback callback2 = new SampleCallback();
            sshClient.execute(command2, callback2);
            Thread.sleep(5000);
            assertTrue(callback2.isDone());
            assertEquals(0, callback2.getStatusCode());
            String result = new String(callback2.getOutput());
            assertTrue(result.contains(SshTestObjectsGenerator.getUserName()));
            System.out.println(new String(callback2.getOutput()));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    class SessionDisconnectInitiator extends Thread {

        public void run() {
            NodeCredentials credentials;
            SshClient client = null;
            try {
                credentials = SshTestObjectsGenerator.createNodeCredentials();
                SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
                client = SshClientBuilder.build(credentials, configuration);
                Thread.sleep(5000);
                SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "sudo reboot");
                SampleCallback callback = new SampleCallback();
                CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
                tracker.start();
                System.out.println("Rebooting the server...");
                client.execute(command, callback);
                tracker.join();
                System.out.println("Reboot Command executed");

            } catch (SshException | InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
            finally {
                if(client != null) {
                    try {
                        client.close();
                    } catch (SshException e) {
                        e.printStackTrace();
                        fail(e.getMessage());
                    }
                }
            }

        }
    }

}
