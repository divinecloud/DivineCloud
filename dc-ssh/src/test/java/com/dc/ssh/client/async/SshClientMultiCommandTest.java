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

package com.dc.ssh.client.async;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.RunAsAttributes;
import com.dc.ssh.client.exec.cmd.SshCommandAttributes;
import com.dc.ssh.client.exec.cmd.script.MultiSshCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptAttributes;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.CommandCompletionTracker;
import com.dc.ssh.client.test.support.SampleCallback;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for multi ssh command execution.
 */
public class SshClientMultiCommandTest {

    @Test
    public void testMultiCmdExecute() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            List<String> cmdStrings = new ArrayList<>();
            cmdStrings.add("whoami");
            cmdStrings.add("echo 'hello SSH World!'");
            MultiSshCommand command = new MultiSshCommand("ID_TEST_" + System.currentTimeMillis(), cmdStrings);
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains(SshTestObjectsGenerator.getUserName()));
            assertTrue(result.contains("hello SSH World!"));
            System.out.println(new String(callback.getOutput()));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testMultiCmdExecuteWithArgumentsAsDifferentUser() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentialsWithLocalCacheSupport();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).cacheLocally("/tmp").readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            List<String> cmdStrings = new ArrayList<>();
            cmdStrings.add("whoami");
            cmdStrings.add("echo $1");
            cmdStrings.add("echo $2");

            RunAsAttributes runAsAttributes = new RunAsAttributes("dcuser", "D1v1n3Cl0ud", false);
            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), runAsAttributes, null, false);
            ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, "Divine Terminal", ScriptLanguage.Shell, "sh", null);

            MultiSshCommand command = new MultiSshCommand(scriptAttributes, cmdStrings);
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            String cmdExecId = sshClient.execute(command, callback);
            tracker.join();
            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            System.out.println(new String(callback.getOutput()));
            assertNotNull(cmdExecId);
            assertTrue(result.contains("dcuser"));
            assertTrue(result.contains("Divine"));
            assertTrue(result.contains("Terminal"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
