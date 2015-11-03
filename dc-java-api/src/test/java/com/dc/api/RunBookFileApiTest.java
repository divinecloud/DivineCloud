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

package com.dc.api;

import com.dc.api.runbook.RunBookApi;
import com.dc.ssh.client.exec.vo.Credential;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.condition.ConditionalBarrier;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RunBookFileApiTest {

    @Test
    public void testRunbookFileExecute() {

        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        List<NodeCredentials> step1List = new ArrayList<>();
        List<NodeCredentials> step2List = new ArrayList<>();

        step1List.add(nodeCredentials1);
        step2List.add(nodeCredentials2);
        List<List<NodeCredentials>> nodesPerStep = new ArrayList<>();
        nodesPerStep.add(step1List);
        nodesPerStep.add(step2List);
        String blockingId = "" + System.nanoTime();
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        SampleRunBookCallBack callback = new SampleRunBookCallBack(barrier, blockingId);

        File runBookFile = new File(TestSupport.getProperty("test.data.path") + "/set1", "sample.runbook");
        api.execute(nodesPerStep, runBookFile, callback, null);
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains("Sample RunBook"));
        assertTrue(callback.getOutputData().contains("sample.key1"));
        assertTrue(callback.getOutputData().contains("sample.key2"));
    }

    @Test
    public void testRunbookFileExecuteWithProperties() {

        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        List<NodeCredentials> step1List = new ArrayList<>();
        List<NodeCredentials> step2List = new ArrayList<>();

        step1List.add(nodeCredentials1);
        step2List.add(nodeCredentials2);
        List<List<NodeCredentials>> nodesPerStep = new ArrayList<>();
        nodesPerStep.add(step1List);
        nodesPerStep.add(step2List);
        String blockingId = "" + System.nanoTime();
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        SampleRunBookCallBack callback = new SampleRunBookCallBack(barrier, blockingId);

        File runBookFile = new File(TestSupport.getProperty("test.data.path") + "/set1", "sample.runbook");
        File propertiesFile = new File(TestSupport.getProperty("test.data.path") + "/set1", "sample.properties");
        api.execute(nodesPerStep, runBookFile, callback, propertiesFile);
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains("Sample RunBook"));
        assertTrue(callback.getOutputData().contains("foo"));
        assertTrue(callback.getOutputData().contains("bar"));
    }

    @Test
    public void testRunbookFileExecuteWithNodeCredFile() {

        RunBookApi api = ApiBuilder.buildRunBookApi(5);

        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();

        List<NodeCredentials> step1List = new ArrayList<>();
        List<NodeCredentials> step2List = new ArrayList<>();

        step1List.add(nodeCredentials1);
        step2List.add(nodeCredentials1);
        List<List<NodeCredentials>> nodesPerStep = new ArrayList<>();
        nodesPerStep.add(step1List);
        nodesPerStep.add(step2List);

        String blockingId = "" + System.nanoTime();
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        SampleRunBookCallBack callback = new SampleRunBookCallBack(barrier, blockingId);

        File runBookFile = new File(TestSupport.getProperty("test.data.path") + "/set1", "sample.runbook");
        File propertiesFile = new File(TestSupport.getProperty("test.data.path") + "/set1", "sample.properties");

        String nodeCredDestinationFolder = TestSupport.getProperty("test.temp.folder") + "/RunBookFileApiTest/" + System.nanoTime() + "/";
        String credFileName =  "nodeCredFile.txt";
        try {
            RunBookApiTestSupport.createNodeCredFile(nodesPerStep, nodeCredDestinationFolder, credFileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        api.execute(new File(nodeCredDestinationFolder, credFileName), runBookFile, callback, propertiesFile);
        barrier.block(blockingId);
        RunBookApiTestSupport.deleteFile(nodeCredDestinationFolder, credFileName);

        assertTrue(callback.getOutputData().contains("Sample RunBook"));
        assertTrue(callback.getOutputData().contains("foo"));
        assertTrue(callback.getOutputData().contains("bar"));
    }


    @Test
    public void testRunbookFileWithCredFileExecution() {
        RunBookApi api = ApiBuilder.buildRunBookApi(5);
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
        String blockingId = "" + System.nanoTime();
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        SampleRunBookCallBack callback = new SampleRunBookCallBack(barrier, blockingId);



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
        FileWriter propsFileWriter = null;
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

        api.execute(new File(nodeCredDestinationFolder, nodeCredFileName), runBookFile, callback, new File(credDestinationFolder, credFileName), propertiesFile);
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains("Nodes Import File Created"));
        assertTrue(callback.getOutputData().contains(TestSupport.getProperty("transient.server1.hostname")));
        assertTrue(callback.getOutputData().contains("Sample Step"));

        RunBookApiTestSupport.deleteFile(testPropsFolder, propertiesFile.getName());
        RunBookApiTestSupport.deleteFile(credDestinationFolder, credFileName);
        RunBookApiTestSupport.deleteFile(nodeCredDestinationFolder, nodeCredFileName);


    }



}
