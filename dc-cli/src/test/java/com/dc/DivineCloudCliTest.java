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

package com.dc;

import com.dc.api.NodeCredentialsGenerator;
import com.dc.api.RunBookApiTestSupport;
import com.dc.api.TestSupport;
import com.dc.runbook.rt.exec.ExecState;
import com.dc.runbook.rt.exec.output.RunBookOutput;
import com.dc.ssh.client.exec.vo.Credential;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.support.KeyValuePair;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DivineCloudCliTest {

    @Test
    public void testPrintMessage() {
        String [] args = new String[] {"cmd"};
        DivineCloudCli.main(args);
    }


    @Test
    public void testParseNodes() {
        String cmdString = "dc-cli -cmd \"<command-string>\" -nodes \"node1, node2, node3\" -a \"arg1,\\\"arg2\\\", arg3, arg4\" -user <username> -key <key-file-path>";
        KeyValuePair<String, String> pair = DivineCloudCli.parseNodes(cmdString);

        KeyValuePair<String, String> pair2 = DivineCloudCli.parseArguments(pair.getValue());

        System.out.println(pair.getKey());
        System.out.println(pair.getValue());
        System.out.println(pair2.getKey());
        System.out.println(pair2.getValue());
    }


    @Test
    public void testCmdExecutePasswordCredentials() {
        String host1 = TestSupport.getProperty("server1.host");
        String userName = TestSupport.getProperty("server1.username");
        String password = TestSupport.getProperty("server1.password");
        String host2 = TestSupport.getProperty("transient.server1.host");
        String pwdFilePath = "/tmp/pwdfile.txt";
        try {
            FileWriter writer = new FileWriter(pwdFilePath);
            writer.write(password);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        String [] args = new String[]{ "-cmd", "hostname", "-nodes", "\" " + host1 + "," + host2 + "\"", "-user", userName, "-pwd", pwdFilePath};

        DivineCloudCli.main(args);

    }

    @Test
    public void testCmdExecuteKeyCredentials() {
        String host = TestSupport.getProperty("server2.host");
        String userName = TestSupport.getProperty("server2.username");
        String keyPath = TestSupport.getProperty("server2.key.path");
        String [] args = new String[]{ "-cmd", "hostname", "-nodes", "\" " + host + "\"", "-user", userName, "-key", keyPath};

        DivineCloudCli.main(args);

    }

    @Test
    public void testRunBookExecute() {
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();

        List<NodeCredentials> step1List = new ArrayList<>();
        List<NodeCredentials> step2List = new ArrayList<>();
        List<NodeCredentials> step3List = new ArrayList<>();

        step1List.add(nodeCredentials1);
        step3List.add(nodeCredentials1);
        List<List<NodeCredentials>> nodesPerStep = new ArrayList<>();
        nodesPerStep.add(step1List);
        nodesPerStep.add(step2List);
        nodesPerStep.add(step3List);

        String nodeCredDestinationFolder = TestSupport.getProperty("test.temp.folder") + "/RunBookFileApiTest/" + System.nanoTime();
        String nodeCredFileName =  "nodeCredFile.txt";
        try {
            RunBookApiTestSupport.createNodeCredFile(nodesPerStep, nodeCredDestinationFolder, nodeCredFileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        File runBookFile = new File(TestSupport.getProperty("test.data.path") + "/set3", "transient-node-sample.runbook");

        String testPropsFolder = TestSupport.getProperty("test.temp.folder") + "/RunBookFileApiTest/props/" + System.nanoTime() + "/";
        File propertiesFile = new File(testPropsFolder, "sample.properties");
        FileWriter propsFileWriter;
        try {
            File testPropsDir = new File(testPropsFolder);
            testPropsDir.mkdirs();
            propsFileWriter = new FileWriter(propertiesFile);
            String propsText = "${SERVER.HOST.1}=" + TestSupport.getProperty("transient.server1.host");
            propsFileWriter.write(propsText);
            propsFileWriter.flush();
            propsFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        String credDestinationFolder = TestSupport.getProperty("test.temp.folder") + "/RunBookFileApiTest/" + System.nanoTime() + "/";
        String credFileName =  "credFile.txt";
        List<Credential> credList = new ArrayList<>();
        Credential cred = new Credential();
        cred.setName("TEST_CRED");
        cred.setUserName(TestSupport.getProperty("transient.server1.username"));
        cred.setPassword(TestSupport.getProperty("transient.server1.password"));
        credList.add(cred);
        try {
            RunBookApiTestSupport.createCredFile(credList, credDestinationFolder, credFileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        String nodesFile = nodeCredDestinationFolder + "/" + nodeCredFileName;
        String credsFile = credDestinationFolder + "/" + credFileName;
        String [] args = new String[]{ "-n", nodesFile, "-runbook", runBookFile.getAbsolutePath(), "-o", "/tmp/routput.txt", "-c", credsFile, "-p", propertiesFile.getAbsolutePath()};

        DivineCloudCli.main(args);

        ObjectMapper reader = new ObjectMapper();
        JavaType type = reader.getTypeFactory().constructType(RunBookOutput.class);
        try {

            RunBookOutput output = reader.readValue(new File("/tmp/routput.txt"), type);
            System.out.println(output.getExecutionId());
            assertNotNull(output.getExecutionId());
            assertEquals(ExecState.SUCCESSFUL, output.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        RunBookApiTestSupport.deleteFile(testPropsFolder, propertiesFile.getName());
        RunBookApiTestSupport.deleteFile(credDestinationFolder, credFileName);
        RunBookApiTestSupport.deleteFile(nodeCredDestinationFolder, nodeCredFileName);
        RunBookApiTestSupport.deleteFile("/tmp", "routput.txt");
    }
}
