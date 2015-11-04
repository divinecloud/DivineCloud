package com.dc.api;

import com.dc.api.runbook.RunBookApi;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.condition.ConditionalBarrier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RunBookApiGeneratedPropertiesTest {

    @Test
    public void testGeneratedProperties() {
        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        RunBook runBook = SampleRunBookGenerator.createRunBookWithGeneratedProperties();

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
        api.execute(nodesPerStep, runBook, callback, null);
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains("sample_generated_property"));

    }
}
