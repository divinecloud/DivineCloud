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
import com.dc.ssh.client.exec.cmd.script.ScriptCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.CommandCompletionTracker;
import com.dc.ssh.client.test.support.SampleCallback;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

public class SshClientRubyScriptTest {

    @Test
    public void testScriptCmdExecute() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            StringBuilder scriptCodeBuilder = new StringBuilder();
            scriptCodeBuilder.append("puts \"Hello Ruby Script Test!\"").append('\n');
            ScriptCommand command = new ScriptCommand("ID_TEST_" + System.nanoTime(), scriptCodeBuilder.toString(), ScriptLanguage.Ruby, "ruby");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));
            System.out.println(new String(callback.getError()));

            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains("Hello Ruby Script Test!"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testScriptCmdCancelExecution() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            StringBuilder scriptCodeBuilder = new StringBuilder();
            scriptCodeBuilder.append("puts \"Hello Ruby Script Test!\"").append('\n');
            scriptCodeBuilder.append("sleep 240").append('\n');
            ScriptCommand command = new ScriptCommand("ID_TEST_" + System.nanoTime(), scriptCodeBuilder.toString(), ScriptLanguage.Ruby, "ruby");
            SampleCallback callback = new SampleCallback();
            System.out.println(command.getExecutionId());
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback, sshClient, 5000, command.getExecutionId());
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));

            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains("Hello Ruby Script Test!"));
            assertEquals(true, callback.isCancelled());
            sshClient.close();

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
