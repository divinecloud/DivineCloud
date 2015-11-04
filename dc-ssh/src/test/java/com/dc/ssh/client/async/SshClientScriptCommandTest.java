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
import com.dc.ssh.client.exec.cmd.script.ScriptAttributes;
import com.dc.ssh.client.exec.cmd.script.ScriptCommand;
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
 * Unit tests for script ssh command execution.
 */
public class SshClientScriptCommandTest {

    @Test
    public void testScriptCmdExecute() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            StringBuilder scriptCodeBuilder = new StringBuilder();
            String folderName = "dc_test_" + System.currentTimeMillis();
            scriptCodeBuilder.append("whoami").append('\n').append("cd /tmp").append('\n').append("mkdir ").append(folderName)
                    .append('\n').append("cd ").append(folderName).append('\n').append("touch abc.txt").append('\n')
                    .append("ls -al").append('\n').append("cd ..").append('\n').append("rm -rf ").append(folderName);
            ScriptCommand command = new ScriptCommand("ID_TEST_" + System.currentTimeMillis(), scriptCodeBuilder.toString(), ScriptLanguage.Shell, "sh");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            long startTime = System.currentTimeMillis();
            sshClient.execute(command, callback);
            tracker.join();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            System.out.println(new String(callback.getOutput()));
            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains(SshTestObjectsGenerator.getUserName()));
            assertTrue(result.contains("abc.txt"));
            assertTrue(!result.contains(folderName));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testScriptCmd2Execute() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            ScriptCommand command = new ScriptCommand("ID_TEST_" + System.currentTimeMillis(), "echo Hello", ScriptLanguage.Shell, "sh");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            long startTime = System.currentTimeMillis();
            sshClient.execute(command, callback);
            tracker.join();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            System.out.println(new String(callback.getOutput()));
            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains("Hello"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }


    @Test
    public void testCancelScriptExecution() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            StringBuilder scriptCodeBuilder = new StringBuilder();

            scriptCodeBuilder.append("#!/bin/bash").append('\n')
                    .append("sh -c \"echo 'Cmd 1'; sleep 50; echo 'Cmd 1 Done'\" &").append('\n')
                    .append("sh -c \"echo 'Cmd 2'; sleep 50; echo 'Cmd 2 Done'\" &").append('\n')
                    .append("sh -c \"echo 'Cmd 3'; sleep 60; echo 'Cmd 3 Done'\" &").append('\n')
                    .append("sh -c \"echo 'Cmd 4'; sleep 60; echo 'Cmd 4 Done'\" &").append('\n')
                    //.append("exec echo 'hi'").append('\n')
                    //.append("exec sleep 90").append('\n')
                    .append("sleep 60").append('\n')
                    .append("echo 'bye'");

            ScriptCommand command = new ScriptCommand("ID_TEST_" + System.currentTimeMillis(), scriptCodeBuilder.toString(), ScriptLanguage.Shell, "sh");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback, sshClient, 5000, command.getExecutionId());
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));
            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains("Cmd"));
            assertTrue(callback.isCancelled());

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }




    }

    @Test
    public void testScriptCmdWithAnswersExecute() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            StringBuilder scriptCodeBuilder = new StringBuilder();
            String folderName = "dc_test_" + System.currentTimeMillis();
            scriptCodeBuilder.append("echo Hello\n").append("echo Enter some text\n").append("read text\n").append("echo You entered: $text");
            List<String> answers = new ArrayList<>();
            answers.add("ABC");
            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), null, answers, false);
            ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, null, ScriptLanguage.Shell, "sh", null);
            ScriptCommand command = new ScriptCommand(scriptAttributes, scriptCodeBuilder.toString());
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));

            String result = new String(callback.getOutput());
            assertTrue(result.contains("ABC"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testScriptCmdExecuteWithArguments() {
        NodeCredentials credentials;
        try {
            credentials = SshTestObjectsGenerator.createNodeCredentials();
            SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
            SshClient sshClient = SshClientBuilder.build(credentials, configuration);
            StringBuilder scriptCodeBuilder = new StringBuilder();
            String folderName = "dc_test_" + System.currentTimeMillis();
            scriptCodeBuilder.append("whoami").append('\n').append("cd /tmp").append('\n').append("mkdir ").append(folderName)
                    .append('\n').append("cd ").append(folderName).append('\n').append("touch abc.txt").append('\n')
                    .append("ls -al").append('\n').append("cd ..").append('\n').append("rm -rf ").append(folderName)
                    .append('\n').append("echo $1 $2");

            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), null, null, false);
            ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, "arg1 arg2", ScriptLanguage.Shell, "sh", null);
            ScriptCommand command = new ScriptCommand(scriptAttributes, scriptCodeBuilder.toString());

            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));

            assertEquals(0, callback.getStatusCode());
            String result = new String(callback.getOutput());
            assertTrue(result.contains(SshTestObjectsGenerator.getUserName()));
            assertTrue(result.contains("abc.txt"));
            assertTrue(result.contains("arg1 arg2"));
            assertTrue(!result.contains(folderName));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testScriptCmdExecuteAsDifferentUser() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            RunAsAttributes runAsAttributes = new RunAsAttributes("dcuser", "D1v1n3Cl0ud", false);
            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), runAsAttributes, null, false);
            ScriptAttributes scriptAttributes = new ScriptAttributes(cmdAttributes, null, ScriptLanguage.Shell, "sh", null);
            ScriptCommand command = new ScriptCommand(scriptAttributes, "whoami" + '\n' +  "echo 'Different user test'");

            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));
            System.out.println(new String(callback.getError()));
            assertEquals(0, callback.getStatusCode());
            assertTrue(new String(callback.getOutput()).contains("dcuser"));
            assertTrue(new String(callback.getOutput()).contains("Different user test"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }



}
