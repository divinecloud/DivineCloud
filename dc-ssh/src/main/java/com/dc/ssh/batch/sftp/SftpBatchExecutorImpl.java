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

package com.dc.ssh.batch.sftp;


import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.SftpStreamer;
import com.dc.support.KeyValuePair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SftpBatchExecutorImpl implements SftpBatchExecutor, ConnectCompleteNotification {
    private Map<String, Cancellable> cancellableServiceMap;
    private SftpBatchContext context;
    private int threadsCount;


    public SftpBatchExecutorImpl(SftpBatchContext context, int threadsCount) {
        this.context = context;
        this.threadsCount = threadsCount;
        initialize();
    }

    private void initialize() {
        cancellableServiceMap = new ConcurrentHashMap<>();
    }

    @Override
    public void executeBatch() throws SftpClientException {

        BatchConnectHandler batchConnectHandler = new BatchConnectHandler(context, threadsCount, this);
        cancellableServiceMap.put(context.getExecutionId(), batchConnectHandler);
        batchConnectHandler.executeBatch();
    }


    @Override
    public void cancel() throws SftpClientException {
        Cancellable cancellable = cancellableServiceMap.get(context.getExecutionId());
        if(cancellable != null) {
            cancellable.cancel();
        }
    }

    @Override
    public void connectionComplete(String executionId, List<KeyValuePair<SshClient, SftpStreamer>> list, List<KeyValuePair<String, String>> failedList) {
        if(failedList != null && failedList.size() > 0) {
            context.getBatchCallback().failed(failedList);
        }
        else {
            SftpBatchTransferHandler batchTransferHandler = new SftpBatchTransferHandler(context, threadsCount, list);
            cancellableServiceMap.put(context.getExecutionId(), batchTransferHandler);
            batchTransferHandler.start();
        }
    }
}
