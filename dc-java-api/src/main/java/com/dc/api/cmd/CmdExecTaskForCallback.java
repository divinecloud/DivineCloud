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

import com.dc.DcException;
import com.dc.api.support.SshClientAccessor;
import com.dc.runbook.rt.cmd.GroupTermCmdRequestType;
import com.dc.runbook.rt.cmd.IndividualCmdExecRequest;
import com.dc.runbook.rt.cmd.exec.ExecutionCallback;
import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.runbook.rt.cmd.exec.SshCommandExecutionCallback;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.ssh.SshCommandExecutor;
import com.dc.runbook.ssh.SshCommandExecutorImpl;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.batch.BatchUnitTask;

public class CmdExecTaskForCallback implements BatchUnitTask {
    private String executionId;
    private DtRunbookStep step;
    private NodeCredentials nodeCred;
    private GroupTermCallback groupTermCallback;
    private SshClientAccessor sshClientAccessor;

    public CmdExecTaskForCallback(String executionId, DtRunbookStep step, NodeCredentials nodeCred, SshClientAccessor sshClientAccessor, GroupTermCallback groupTermCallback) {
        this.executionId = executionId;
        this.step = step;
        this.nodeCred = nodeCred;
        this.groupTermCallback = groupTermCallback;
        this.sshClientAccessor = sshClientAccessor;
    }

    @Override
    public void execute() throws DcException {
        SshClient sshClient = sshClientAccessor.provide(nodeCred);
        IndividualCmdExecRequest request = prepareIndividualCmdExecRequest(sshClient);
        SshCommandExecutor executor = new SshCommandExecutorImpl();
        executor.execute(request);
    }

    private IndividualCmdExecRequest prepareIndividualCmdExecRequest(SshClient sshClient) {
        IndividualCmdExecRequest request = new IndividualCmdExecRequest();
        request.setType(GroupTermCmdRequestType.EXEC);
        request.setSshClient(sshClient);
        ExecutionCallback callback = new SshCommandExecutionCallback(nodeCred.getId(), groupTermCallback);
        request.setCallback(callback);
        request.setCommand(step);
        request.setExecutionId(executionId);
        request.setType(GroupTermCmdRequestType.EXEC);
        return request;
    }

}