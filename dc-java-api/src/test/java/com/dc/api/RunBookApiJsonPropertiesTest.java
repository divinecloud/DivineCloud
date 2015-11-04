package com.dc.api;

import com.dc.api.runbook.RunBookApi;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.condition.ConditionalBarrier;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RunBookApiJsonPropertiesTest {

    @Test
    public void testRunbookFileExecuteWithJsonProperties() {

        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        List<NodeCredentials> step1List = new ArrayList<>();
        List<NodeCredentials> step2List = new ArrayList<>();
        List<NodeCredentials> step3List = new ArrayList<>();

        step1List.add(nodeCredentials1);
        step2List.add(nodeCredentials2);
        step3List.add(nodeCredentials1);
        List<List<NodeCredentials>> nodesPerStep = new ArrayList<>();
        nodesPerStep.add(step1List);
        nodesPerStep.add(step2List);
        nodesPerStep.add(step3List);

        String blockingId = "" + System.nanoTime();
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        SampleRunBookCallBack callback = new SampleRunBookCallBack(barrier, blockingId);

        File runBookFile = new File(TestSupport.getProperty("test.data.path") + "/set2", "sample-json-properties.runbook");
        File propertiesFile = new File(TestSupport.getProperty("test.data.path") + "/set2", "properties.json");
        api.execute(nodesPerStep, runBookFile, callback, propertiesFile);
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains("Sample Json Properties RunBook"));
        assertTrue(callback.getOutputData().contains("prop1"));
        assertTrue(callback.getOutputData().contains("prop2"));
    }
}
