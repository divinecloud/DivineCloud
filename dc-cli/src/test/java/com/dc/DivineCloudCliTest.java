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
import com.dc.support.ExecutionOutput;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DivineCloudCliTest {

    @Test
    public void testCmdExecutePasswordCredentials() {
        String host1 = TestSupport.getProperty("server1.host");
        String userName = TestSupport.getProperty("server1.username");
        String password = TestSupport.getProperty("server1.password");
        String pwdFilePath = "/tmp/pwdfile.txt";
        String outputFileName = "cmd-output" + System.nanoTime() + ".txt";
        String outputFilePath = "/tmp/" + outputFileName;

        writeToFile(password, pwdFilePath);

        String [] args = new String[]{ "-cmd", "hostname", "-nodes", host1, "-user", userName, "-pwd", pwdFilePath, "-o", outputFilePath};

        DivineCloudCli.main(args);
        ObjectMapper reader = new ObjectMapper();
        JavaType type = reader.getTypeFactory().constructType(ExecutionOutput.class);
        try {

            ExecutionOutput output = reader.readValue(new File(outputFilePath), type);

            assertNotNull(output);
            assertNotNull(output.getOutputMap());
            Map<String, Integer> statusCodeMap = output.getStatusCodeMap();
            assertNotNull(statusCodeMap);
            assertEquals(new Integer(0), statusCodeMap.get(host1));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void writeToFile(String password, String pwdFilePath) {
        try {
            FileWriter writer = new FileWriter(pwdFilePath);
            writer.write(password);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void testCmdExecutePasswordCredentialsForMultipleHosts() {
        String host1 = TestSupport.getProperty("server1.host");
        String userName = TestSupport.getProperty("server1.username");
        String password = TestSupport.getProperty("server1.password");
        String host2 = TestSupport.getProperty("transient.server1.host");
        String pwdFilePath = "/tmp/pwdfile.txt";
        String outputFileName = "cmd-output" + System.nanoTime() + ".txt";
        String outputFilePath = "/tmp/" + outputFileName;

        writeToFile(password, pwdFilePath);

        String [] args = new String[]{ "-cmd", "hostname", "-nodes", "\" " + host1 + "," + host2 + "\"", "-user", userName, "-pwd", pwdFilePath, "-o", outputFilePath};

        DivineCloudCli.main(args);
        ObjectMapper reader = new ObjectMapper();
        JavaType type = reader.getTypeFactory().constructType(ExecutionOutput.class);
        try {

            ExecutionOutput output = reader.readValue(new File(outputFilePath), type);

            assertNotNull(output);
            assertNotNull(output.getOutputMap());
            Map<String, Integer> statusCodeMap = output.getStatusCodeMap();
            assertNotNull(statusCodeMap);
            assertEquals(new Integer(0), statusCodeMap.get(host1));
            assertEquals(new Integer(0), statusCodeMap.get(host2));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testMultiCmdExecutePasswordCredentials() {
        String host1 = TestSupport.getProperty("server1.host");
        String userName = TestSupport.getProperty("server1.username");
        String password = TestSupport.getProperty("server1.password");
        String host2 = TestSupport.getProperty("transient.server1.host");
        String pwdFilePath = "/tmp/pwdfile.txt";
        String outputFileName = "cmd-output" + System.nanoTime() + ".txt";
        String outputFilePath = "/tmp/" + outputFileName;
        writeToFile(password, pwdFilePath);

        String [] args = new String[]{ "-cmd", "\"hostname; echo Hello\"", "-nodes", "\" " + host1 + "," + host2 + "\"", "-user", userName, "-pwd", pwdFilePath, "-o", outputFilePath};

        DivineCloudCli.main(args);

        ObjectMapper reader = new ObjectMapper();
        JavaType type = reader.getTypeFactory().constructType(ExecutionOutput.class);
        try {

            ExecutionOutput output = reader.readValue(new File(outputFilePath), type);

            assertNotNull(output);
            assertNotNull(output.getOutputMap());
            Map<String, Integer> statusCodeMap = output.getStatusCodeMap();
            assertNotNull(statusCodeMap);
            assertEquals(new Integer(0), statusCodeMap.get(host1));
            assertEquals(new Integer(0), statusCodeMap.get(host2));
            assertTrue(output.getOutputMap().get(host1).contains("Hello"));
            assertTrue(output.getOutputMap().get(host2).contains("Hello"));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testCmdExecuteKeyCredentials() {
        String host = TestSupport.getProperty("server2.host");
        String userName = TestSupport.getProperty("server2.username");
        String keyPath = TestSupport.getProperty("server2.key.path");
        String outputFileName = "cmd-output" + System.nanoTime() + ".txt";
        String outputFilePath = "/tmp/" + outputFileName;
        String [] args = new String[]{ "-cmd", "\"hostname; echo Hello\"", "-nodes", "\" " + host + "\"", "-user", userName, "-key", keyPath, "-o", outputFilePath};
        DivineCloudCli.main(args);

        ObjectMapper reader = new ObjectMapper();
        JavaType type = reader.getTypeFactory().constructType(ExecutionOutput.class);
        try {

            ExecutionOutput output = reader.readValue(new File(outputFilePath), type);

            assertNotNull(output);
            assertNotNull(output.getOutputMap());
            Map<String, Integer> statusCodeMap = output.getStatusCodeMap();
            assertNotNull(statusCodeMap);
            assertEquals(new Integer(0), statusCodeMap.get(host));
            assertTrue(output.getOutputMap().get(host).contains("Hello"));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testScriptExecutePasswordCredentials() {
        String host1 = TestSupport.getProperty("server1.host");
        String userName = TestSupport.getProperty("server1.username");
        String password = TestSupport.getProperty("server1.password");
        String host2 = TestSupport.getProperty("transient.server1.host");
        String pwdFilePath = "/tmp/pwdfile.txt";
        File scriptFile = new File(TestSupport.getProperty("test.data.path") + "/set1", "sample.sh");

        writeToFile(password, pwdFilePath);
        String outputFileName = "cmd-output" + System.nanoTime() + ".txt";
        String outputFilePath = "/tmp/" + outputFileName;

        String [] args = new String[]{ "-script", scriptFile.getAbsolutePath(), "-nodes", "\" " + host1 + "," + host2 + "\"", "-user", userName, "-pwd", pwdFilePath, "-o", outputFilePath};

        DivineCloudCli.main(args);

        ObjectMapper reader = new ObjectMapper();
        JavaType type = reader.getTypeFactory().constructType(ExecutionOutput.class);
        try {

            ExecutionOutput output = reader.readValue(new File(outputFilePath), type);

            assertNotNull(output);
            assertNotNull(output.getOutputMap());
            Map<String, Integer> statusCodeMap = output.getStatusCodeMap();
            assertNotNull(statusCodeMap);
            assertEquals(new Integer(0), statusCodeMap.get(host1));
            assertEquals(new Integer(0), statusCodeMap.get(host2));
            assertTrue(output.getOutputMap().get(host1).contains("Sample Shell Script"));
            assertTrue(output.getOutputMap().get(host2).contains("Sample Shell Script"));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

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

        /*
        RunBookApiTestSupport.deleteFile(testPropsFolder, propertiesFile.getName());
        RunBookApiTestSupport.deleteFile(credDestinationFolder, credFileName);
        RunBookApiTestSupport.deleteFile(nodeCredDestinationFolder, nodeCredFileName);
        RunBookApiTestSupport.deleteFile("/tmp", "routput.txt");
        */
    }
}
