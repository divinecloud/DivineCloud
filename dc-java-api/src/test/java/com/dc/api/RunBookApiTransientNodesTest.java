package com.dc.api;

import com.dc.api.runbook.RunBookApi;
import com.dc.node.NodeDetails;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.rt.CredentialsProvider;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.condition.ConditionalBarrier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RunBookApiTransientNodesTest {

    @Test
    public void testTransientNodeExecution() {
        RunBookApi api = ApiBuilder.buildRunBookApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        RunBook runBook = SampleRunBookGenerator.createRunBookWithTransientNodesSupport();

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
        CredentialsProvider credentialsProvider = new CredentialsProviderTestImpl();
        api.execute(nodesPerStep, runBook, callback, credentialsProvider, null);
        barrier.block(blockingId);
        assertTrue(callback.getOutputData().contains(TestSupport.getProperty("server1.username")));
        assertTrue(callback.getOutputData().contains(TestSupport.getProperty("transient.server1.hostname")));

    }

    class CredentialsProviderTestImpl implements CredentialsProvider {

        @Override
        public NodeCredentials provide(NodeDetails nodeDetails) {
            String host = TestSupport.getProperty("transient.server1.host");
            String username = TestSupport.getProperty("transient.server1.username");
            String password = TestSupport.getProperty("transient.server1.password");
            return new NodeCredentials.Builder(host, username).id(host).password(password).build();
        }
    }
}
