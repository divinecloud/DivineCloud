package com.dc.api;

import com.dc.api.runbook.RunBookApi;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.condition.ConditionalBarrier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RunBookApiCancelTest {

    @Test
    public void testCancel() {
        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        RunBook runBook = SampleRunBookGenerator.createLongRunningRunBook();

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
        String executionId = api.execute(nodesPerStep, runBook, callback, null);
        try {
            Thread.sleep(8000);
            api.cancel(executionId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains("Sample Script"));
        assertTrue(!callback.getOutputData().contains("arg1"));

    }
}
