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

package com.dc.ssh.batch.sftp;


import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.SftpStreamer;
import com.dc.support.KeyValuePair;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchConnectHandler implements TaskCompleteNotification, Cancellable {
    private SftpBatchContext context;
    private SftpBatchExecutorService service;
    private Map<String, KeyValuePair<Integer, AtomicInteger>> executionCompletionCountMap;
    private int threadsCount;
    private List<KeyValuePair<SshClient, SftpStreamer>> list;
    private List<KeyValuePair<String, String>> failedNodesList;
    private volatile boolean executionStarted;
    private ConnectCompleteNotification connectCompleteNotification;

    public BatchConnectHandler(SftpBatchContext context, int threadsCount, ConnectCompleteNotification connectCompleteNotification) {
        this.context = context;
        this.threadsCount = threadsCount;
        this.connectCompleteNotification = connectCompleteNotification;
        initialize();
    }

    private void initialize() {
        list = new Vector<>();
        failedNodesList = new Vector<>();
        executionCompletionCountMap = new ConcurrentHashMap<>();
        executionCompletionCountMap.put(context.getExecutionId(), new KeyValuePair<>(context.getSshClients().size(), new AtomicInteger()));
        service = new SftpBatchExecutorService(context.getExecutionId(), threadsCount, this, false);
    }

    @Override
    public void cancel() {
        service.close();
        context.getBatchCallback().cancelled();
    }

    public synchronized void executeBatch() {
        if(!executionStarted) {
            executionStarted = true;
        }
        else {
            throw new SftpClientException("Execution already in progress");
        }

        for(SshClient client : context.getSshClients()) {
            SftpConnectionTask task = new SftpConnectionTask(client, list, failedNodesList);
            service.submit(task);
        }
    }

    @Override
    public void taskComplete(String id) {
        KeyValuePair<Integer, AtomicInteger> pair = executionCompletionCountMap.get(id);
        if(pair != null) {
            int count = pair.getValue().incrementAndGet();
            if(count == pair.getKey()) {
                service.close();
                connectCompleteNotification.connectionComplete(context.getExecutionId(), list, failedNodesList);

            }
        }
    }

    class SftpConnectionTask implements Callable {
        private SshClient client;
        private List<KeyValuePair<SshClient, SftpStreamer>> transferServiceList;
        private List<KeyValuePair<String, String>> failedNodesList;

        public SftpConnectionTask(SshClient client, List<KeyValuePair<SshClient, SftpStreamer>> transferServiceList, List<KeyValuePair<String, String>> failedNodesList) {
            this.client = client;
            this.transferServiceList = transferServiceList;
            this.failedNodesList = failedNodesList;
        }

        @Override
        public Object call() throws Exception {
            String path;
            if(context.getMode() == SftpMode.UPLOAD) {
                path = context.getTo();
            }
            else {
                path = context.getFrom();
            }
            try {
                SftpStreamer streamer = client.getSftpStreamer(context.getMode(), path);
                transferServiceList.add(new KeyValuePair<>(client, streamer));
            }
            catch (Throwable t) {
                t.printStackTrace();
                failedNodesList.add(new KeyValuePair<>(client.id(), t.getMessage()));
            }
            return client.id();
        }
    }
}

