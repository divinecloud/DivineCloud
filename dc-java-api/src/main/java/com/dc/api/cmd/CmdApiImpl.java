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

package com.dc.api.cmd;

import com.dc.api.exec.NodeExecutionDetails;
import com.dc.api.support.ExecutionIdGenerator;
import com.dc.api.support.SshClientAccessor;
import com.dc.runbook.rt.cmd.GroupTermCmdRequestType;
import com.dc.runbook.rt.cmd.IndividualCmdCancelRequest;
import com.dc.runbook.rt.cmd.exec.GroupCmdCancelTask;
import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.rt.domain.item.DtRunbookCommand;
import com.dc.runbook.rt.domain.item.DtRunbookItem;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.SshCommand;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.batch.BatchExecutorService;
import com.dc.util.batch.BatchUnitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmdApiImpl implements CmdApi {

    private SshClientAccessor sshClientAccessor;
    private int batchSize;
    private ExecutionIdGenerator idGenerator;

    public CmdApiImpl(int batchSize) {
        this.batchSize = batchSize;
        this.sshClientAccessor = new SshClientAccessor();
        idGenerator = new ExecutionIdGenerator();
    }

    public List<NodeExecutionDetails> execute(List<NodeCredentials> nodeCredentials, String command) {
        List<NodeExecutionDetails> result = new ArrayList<>();
        List<BatchUnitTask> taskList = new ArrayList<>();

        for(NodeCredentials nodeCred : nodeCredentials) {
            CmdExecTask task = new CmdExecTask(sshClientAccessor, nodeCred, command);
            taskList.add(task);
        }

        BatchExecutorService batchExecutorService = new BatchExecutorService(batchSize, taskList);

        try {
            batchExecutorService.execute();
            result.addAll(taskList.stream().map(task -> ((CmdExecTask) task).getResult()).collect(Collectors.toList()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String execute(List<NodeCredentials> nodeCredentials, String command, GroupTermCallback groupTermCallback) {
        List<BatchUnitTask> requestList = new ArrayList<>();
        String execId = idGenerator.next();
        GroupTermCallbackWrapper callbackWrapper = new GroupTermCallbackWrapper(groupTermCallback, nodeCredentials.size());
        for (NodeCredentials nodeCred : nodeCredentials) {
            DtRunbookStep step = convertToRunbookStep(command, execId);
            CmdExecTaskForCallback task = new CmdExecTaskForCallback(execId, step, nodeCred, sshClientAccessor, callbackWrapper);
            requestList.add(task);
        }
        BatchExecutorService batchExecutorService = new BatchExecutorService(batchSize, requestList);
        try {
            batchExecutorService.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return execId;
    }

    private DtRunbookStep convertToRunbookStep(String command, String execId) {
        DtRunbookStep step = new DtRunbookStep();
        DtRunbookItem item = new DtRunbookCommand(1, 1, command, null, false);
        step.setItem(item);
        return step;
    }

    @Override
    public List<NodeExecutionDetails> execute(List<NodeCredentials> nodeCredentials, SshCommand command) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public String execute(List<NodeCredentials> nodeCredentials, SshCommand command, GroupTermCallback callback) {
        return null;
    }

    @Override
    public void cancel(List<NodeCredentials> nodeCredentials, String executionId) {
        List<BatchUnitTask> cancelBatchTaskList = new ArrayList<>();
        for (NodeCredentials node : nodeCredentials) {
            SshClient sshClient = sshClientAccessor.get(node.getId());
            if(sshClient != null) {
                IndividualCmdCancelRequest cancelCmdRequest = prepareIndividualCmdCancelRequest(executionId, sshClient);
                BatchUnitTask task = new GroupCmdCancelTask(cancelCmdRequest);
                if (cancelCmdRequest.getSshClient() != null) {
                    cancelBatchTaskList.add(task);
                }
            }
        }
        if (cancelBatchTaskList.size() > 0) {
            BatchExecutorService batchExecutorService = new BatchExecutorService(100, cancelBatchTaskList);
            try {
                batchExecutorService.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO: Later add logic to inform calling code
            }
        }
    }

    private IndividualCmdCancelRequest prepareIndividualCmdCancelRequest(String executionId, SshClient sshClient) {
        IndividualCmdCancelRequest request = new IndividualCmdCancelRequest();
        request.setExecutionId(executionId);
        request.setType(GroupTermCmdRequestType.CANCEL);
        request.setSshClient(sshClient);
        return request;
    }


}
