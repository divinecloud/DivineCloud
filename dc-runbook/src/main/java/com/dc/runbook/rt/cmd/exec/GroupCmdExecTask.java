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

package com.dc.runbook.rt.cmd.exec;

import com.dc.DcException;
import com.dc.runbook.rt.cmd.GroupTermCmdRequestType;
import com.dc.runbook.rt.cmd.IndividualCmdExecRequest;
import com.dc.runbook.rt.domain.DtRunbookStep;
import com.dc.runbook.ssh.SshCommandExecutor;
import com.dc.runbook.ssh.SshCommandExecutorImpl;
import com.dc.ssh.client.exec.SshClient;
import com.dc.util.batch.BatchUnitTask;

public class GroupCmdExecTask implements BatchUnitTask {
    private String executionId;
    private DtRunbookStep step;
    private String nodeId;
    private GroupTermCallback	groupTermCallback;
    private SshClient sshClient;

    public GroupCmdExecTask(String executionId, DtRunbookStep step, String nodeId, SshClient sshClient, GroupTermCallback groupTermCallback) {
        this.executionId = executionId;
        this.step = step;
        this.nodeId = nodeId;
        this.groupTermCallback = groupTermCallback;
        this.sshClient = sshClient;
    }

    @Override
    public void execute() throws DcException {
        IndividualCmdExecRequest request = prepareIndividualCmdExecRequest();
        SshCommandExecutor executor = new SshCommandExecutorImpl();
        executor.execute(request);
    }

    private IndividualCmdExecRequest prepareIndividualCmdExecRequest() {
        IndividualCmdExecRequest request = new IndividualCmdExecRequest();
        request.setType(GroupTermCmdRequestType.EXEC);
        request.setSshClient(sshClient);
        ExecutionCallback callback = new SshCommandExecutionCallback(nodeId, groupTermCallback);
        request.setCallback(callback);
        request.setCommand(step);
        request.setExecutionId(executionId);
        request.setType(GroupTermCmdRequestType.EXEC);
        return request;
    }

}
