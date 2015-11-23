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

package com.dc.api.cmd;

import com.dc.DcException;
import com.dc.api.exec.NodeExecutionDetails;
import com.dc.api.support.SshClientAccessor;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.SshCommand;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.batch.BatchUnitTask;

import java.util.concurrent.CountDownLatch;

public class CmdExecTask  implements BatchUnitTask {
    private SshClientAccessor sshClientAccessor;
    private NodeCredentials nodeCred;
    private SshCommand command;
    private NodeExecutionDetails details;
    private CountDownLatch doneSignal;

    public CmdExecTask(SshClientAccessor sshClientAccessor, NodeCredentials nodeCred, SshCommand command, CountDownLatch doneSignal) {
        this.sshClientAccessor = sshClientAccessor;
        this.nodeCred = nodeCred;
        this.command = command;
        this.doneSignal = doneSignal;
    }

    public NodeExecutionDetails getResult() {
        return details;
    }

    @Override
    public void execute() throws DcException {
        SshClient sshClient = sshClientAccessor.provide(nodeCred);
        CmdExecCallback callback = new CmdExecCallback();
        sshClient.execute(command, callback);
        ExecutionDetails execDetails =  new ExecutionDetails(callback.getStatusCode(), callback.getOutput(), callback.getError());
        details = new NodeExecutionDetails(nodeCred, execDetails);
        doneSignal.countDown();
    }
}