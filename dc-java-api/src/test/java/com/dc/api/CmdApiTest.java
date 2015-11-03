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

import com.dc.api.cmd.CmdApi;
import com.dc.api.exec.NodeExecutionDetails;
import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.condition.ConditionalBarrier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CmdApiTest {

    @Test
    public void testExecuteCmd() {
        CmdApi api = ApiBuilder.buildCmdApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();
        String cmd = "hostname";
        List<NodeCredentials> list = new ArrayList<>();
        list.add(nodeCredentials1);
        list.add(nodeCredentials2);
        List<NodeExecutionDetails> result = api.execute(list, cmd);
        assertNotNull(result);
        assertEquals(2, result.size());
        System.out.println(new String(result.get(0).getExecutionDetails().getOutput()));
        System.out.println(new String(result.get(1).getExecutionDetails().getOutput()));

        String cmd2 = "whoami";
        result = api.execute(list, cmd2);
        assertNotNull(result);
        assertEquals(2, result.size());
        System.out.println(new String(result.get(0).getExecutionDetails().getOutput()));
        System.out.println(new String(result.get(1).getExecutionDetails().getOutput()));
    }

    @Test
    public void testExecuteCmdWithCallback() {
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        String blockingId = "BlockingID" + System.currentTimeMillis();
        GroupTermCallbackTestImpl groupTermCallback = new GroupTermCallbackTestImpl(barrier, blockingId, 2);
        CmdApi api = ApiBuilder.buildCmdApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();
        String expectedOutput = "Test";
        String cmd = "echo " + expectedOutput;
        List<NodeCredentials> list = new ArrayList<>();
        list.add(nodeCredentials1);
        list.add(nodeCredentials2);
        String execId = api.execute(list, cmd, groupTermCallback);
        assertNotNull(execId);
        barrier.block(blockingId);

        Map<String, String> outputMap = groupTermCallback.getOutputMap();
        for (String output : outputMap.values()) {
            assertEquals(expectedOutput, output);
        }
    }


    @Test
    public void testExecuteCmdWithCallbackConcurrent() {
        CmdApi api = ApiBuilder.buildCmdApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        String blockingId1 = "BlockingID1";
        GroupTermCallbackTestImpl groupTermCallback1 = new GroupTermCallbackTestImpl(barrier, blockingId1, 2);
        String expectedOutput1 = "Test1";
        CmdExecutorThread thread1 = new CmdExecutorThread(api, nodeCredentials1, nodeCredentials2, groupTermCallback1, expectedOutput1);

        String blockingId2 = "BlockingID2";
        GroupTermCallbackTestImpl groupTermCallback2 = new GroupTermCallbackTestImpl(barrier, blockingId2, 2);
        String expectedOutput2 = "Test2";
        CmdExecutorThread thread2 = new CmdExecutorThread(api, nodeCredentials1, nodeCredentials2, groupTermCallback2, expectedOutput2);
        thread1.start();
        thread2.start();

        barrier.block(blockingId1);
        barrier.block(blockingId2);

        Map<String, String> outputMap = groupTermCallback1.getOutputMap();
        for (String output : outputMap.values()) {
            assertEquals(expectedOutput1, output);
        }

        outputMap = groupTermCallback2.getOutputMap();
        for (String output : outputMap.values()) {
            assertEquals(expectedOutput2, output);
        }
    }


    @Test
    public void testExecuteCmdWithCallbackCancelAction() {
        CmdApi api = ApiBuilder.buildCmdApi(5);
        NodeCredentials nodeCredentials1 = NodeCredentialsGenerator.generateServer1Credentials();
        NodeCredentials nodeCredentials2 = NodeCredentialsGenerator.generateServer2Credentials();

        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        String blockingId1 = "BlockingID1";
        CancelActionGroupTermCallback groupTermCallback1 = new CancelActionGroupTermCallback(barrier, blockingId1, 2);
        String expectedOutput1 = "Test1";
        LongRunningCmdExecutorThread thread1 = new LongRunningCmdExecutorThread(api, nodeCredentials1, nodeCredentials2, groupTermCallback1, expectedOutput1);

        thread1.start();

        boolean wait = true;

        while (wait) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (thread1.getExecId() != null) {
                wait = false;
            }
        }

        List<NodeCredentials> list = new ArrayList<>();
        list.add(nodeCredentials1);
        list.add(nodeCredentials2);
        CancellingExecutionThread cancellingThread = new CancellingExecutionThread(list, thread1.getExecId(), api);
        cancellingThread.start();

        barrier.block(blockingId1);


        Map<String, String> outputMap = groupTermCallback1.getOutputMap();
        for (String output : outputMap.values()) {
            assertEquals(expectedOutput1, output);
        }
    }


    class GroupTermCallbackTestImpl implements GroupTermCallback {
        private ConditionalBarrier<String> barrier;
        private String id;
        private AtomicInteger totalNodes;
        private Map<String, String> outputMap;

        public GroupTermCallbackTestImpl(ConditionalBarrier<String> barrier, String id, int totalNodesCount) {
            this.barrier = barrier;
            this.id = id;
            totalNodes = new AtomicInteger(totalNodesCount);
            outputMap = new ConcurrentHashMap<>();
        }

        @Override
        public void complete(String nodeDisplayId, int statusCode) {
            System.out.println("Execution complete for " + nodeDisplayId);
            int remaining = totalNodes.decrementAndGet();
            if (remaining == 0) {
                done();
            }
        }

        @Override
        public void output(String displayId, String output) {
            System.out.println(displayId + " Output : " + output);
            if (output != null && output.trim().length() > 0) {
                String actualOutput = outputMap.get(displayId);
                if (actualOutput == null) {
                    actualOutput = "";
                }
                actualOutput += output.trim();
                outputMap.put(displayId, actualOutput);
            }
        }

        public Map<String, String> getOutputMap() {
            return outputMap;
        }

        @Override
        public void error(String displayId, String error) {
            System.out.println(displayId + " Error : " + error);
        }

        @Override
        public void started() {

        }

        @Override
        public void markCancelled() {

        }

        @Override
        public void done() {
            barrier.release(id);
        }

        @Override
        public void done(Exception e) {
            e.printStackTrace();
            barrier.release(id);
        }
    }

    class CmdExecutorThread extends Thread {
        private CmdApi api;
        private NodeCredentials nodeCredentials1;
        private NodeCredentials nodeCredentials2;
        private GroupTermCallback groupTermCallback;
        private String expectedOutput;

        public CmdExecutorThread(CmdApi api, NodeCredentials nodeCredentials1, NodeCredentials nodeCredentials2, GroupTermCallback groupTermCallback, String expectedOutput) {
            this.api = api;
            this.nodeCredentials1 = nodeCredentials1;
            this.nodeCredentials2 = nodeCredentials2;
            this.groupTermCallback = groupTermCallback;
            this.expectedOutput = expectedOutput;
        }

        public void run() {

            String echoStr = expectedOutput;
            String cmd = "echo " + echoStr;
            List<NodeCredentials> list = new ArrayList<>();
            list.add(nodeCredentials1);
            list.add(nodeCredentials2);
            String execId = api.execute(list, cmd, groupTermCallback);
            assertNotNull(execId);
        }

    }

    class CancellingExecutionThread extends Thread {
        private List<NodeCredentials> list;
        private String executionId;
        private CmdApi api;

        public CancellingExecutionThread(List<NodeCredentials> list, String executionId, CmdApi api) {
            this.list = list;
            this.executionId = executionId;
            this.api = api;
        }

        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Cancelling execution for ID : " + executionId);
            api.cancel(list, executionId);
        }
    }


    class LongRunningCmdExecutorThread extends Thread {
        private CmdApi api;
        private NodeCredentials nodeCredentials1;
        private NodeCredentials nodeCredentials2;
        private GroupTermCallback groupTermCallback;
        private String expectedOutput;
        private String execId;

        public LongRunningCmdExecutorThread(CmdApi api, NodeCredentials nodeCredentials1, NodeCredentials nodeCredentials2, GroupTermCallback groupTermCallback, String expectedOutput) {
            this.api = api;
            this.nodeCredentials1 = nodeCredentials1;
            this.nodeCredentials2 = nodeCredentials2;
            this.groupTermCallback = groupTermCallback;
            this.expectedOutput = expectedOutput;
        }

        public void run() {
            String echoStr = expectedOutput;
            String cmd = "echo " + echoStr + " ; " + "sleep 60; " + "pwd";
            List<NodeCredentials> list = new ArrayList<>();
            list.add(nodeCredentials1);
            list.add(nodeCredentials2);
            execId = api.execute(list, cmd, groupTermCallback);
            assertNotNull(execId);
        }

        public String getExecId() {
            return execId;
        }

    }

    class CancelActionGroupTermCallback implements GroupTermCallback {
        private ConditionalBarrier<String> barrier;
        private String id;
        //private AtomicInteger totalNodes;
        private Map<String, String> outputMap;
        private volatile boolean cancelled;

        public CancelActionGroupTermCallback(ConditionalBarrier<String> barrier, String id, int totalNodesCount) {
            this.barrier = barrier;
            this.id = id;
            //totalNodes = new AtomicInteger(totalNodesCount);
            outputMap = new ConcurrentHashMap<>();
        }

        @Override
        public void complete(String nodeDisplayId, int statusCode) {
            System.out.println("Execution complete for " + nodeDisplayId);
//            int remaining = totalNodes.decrementAndGet();
//            if (remaining == 0) {
//                done();
//            }
        }

        @Override
        public void output(String displayId, String output) {
            System.out.println(displayId + " Output : " + output);
            if (output != null && output.trim().length() > 0) {
                String actualOutput = outputMap.get(displayId);
                if (actualOutput == null) {
                    actualOutput = "";
                }
                actualOutput += output.trim();
                outputMap.put(displayId, actualOutput);
            }
        }

        public Map<String, String> getOutputMap() {
            return outputMap;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void error(String displayId, String error) {
            System.out.println(displayId + " Error : " + error);
        }

        @Override
        public void started() {

        }

        @Override
        public void markCancelled() {
            cancelled = true;
        }

        @Override
        public void done() {
            barrier.release(id);
        }

        @Override
        public void done(Exception e) {
            e.printStackTrace();
            barrier.release(id);
        }
    }

}