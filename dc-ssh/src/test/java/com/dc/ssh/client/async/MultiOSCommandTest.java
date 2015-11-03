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

import com.dc.LinuxOSType;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.RunAsAttributes;
import com.dc.ssh.client.exec.cmd.SshCommandAttributes;
import com.dc.ssh.client.exec.cmd.script.MultiOSCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptAttributes;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.CommandCompletionTracker;
import com.dc.ssh.client.test.support.SampleCallback;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import com.dc.support.KeyValuePair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MultiOSCommandTest {

    @Test
    public void testMultiOSCmd() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            List<KeyValuePair<String, LinuxOSType>> cmdStrings = new ArrayList<>();
            KeyValuePair<String, LinuxOSType> ubuntuCmdPair = new KeyValuePair<>("echo 'Ubuntu Cmd Ran'", LinuxOSType.Ubuntu);
            KeyValuePair<String, LinuxOSType> centosCmdPair = new KeyValuePair<>("echo 'CentOS Cmd Ran'", LinuxOSType.CentOS);
            KeyValuePair<String, LinuxOSType> amznCmdPair = new KeyValuePair<>("echo 'AMZN Cmd Ran'", LinuxOSType.Amazon);
            cmdStrings.add(ubuntuCmdPair);
            cmdStrings.add(centosCmdPair);
            cmdStrings.add(amznCmdPair);

            MultiOSCommand command = new MultiOSCommand("ID_TEST_" + System.currentTimeMillis(), cmdStrings);
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains("CentOS Cmd Ran"));
            System.out.println(new String(callback.getOutput()));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testMultiOSCmdExecuteWithArgumentsAsDifferentUser() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentialsWithLocalCacheSupport();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).cacheLocally("/tmp").readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            List<KeyValuePair<String, LinuxOSType>> cmdStrings = new ArrayList<>();
            KeyValuePair<String, LinuxOSType> ubuntuCmdPair = new KeyValuePair<>("whoami; echo 'Ubuntu Cmd Ran'; echo $1", LinuxOSType.Ubuntu);
            KeyValuePair<String, LinuxOSType> centosCmdPair = new KeyValuePair<>("whoami; echo 'CentOS Cmd Ran'; echo $1", LinuxOSType.CentOS);
            KeyValuePair<String, LinuxOSType> amznCmdPair = new KeyValuePair<>("whoami; echo 'AMZN Cmd Ran'; echo $1", LinuxOSType.Amazon);
            cmdStrings.add(ubuntuCmdPair);
            cmdStrings.add(centosCmdPair);
            cmdStrings.add(amznCmdPair);

            RunAsAttributes runAsAttributes = new RunAsAttributes("dcuser", "D1v1n3Cl0ud", false);
            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), runAsAttributes, null, false);
            ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, "DivineTerminal", ScriptLanguage.Shell, "sh", null);

            MultiOSCommand command = new MultiOSCommand(scriptAttributes, cmdStrings);
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            String cmdExecId = sshClient.execute(command, callback);
            tracker.join();
            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            System.out.println(new String(callback.getOutput()));
            assertNotNull(cmdExecId);
            assertTrue(result.contains("CentOS Cmd Ran"));
            assertTrue(result.contains("DivineTerminal"));
            assertTrue(result.contains("dcuser"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
