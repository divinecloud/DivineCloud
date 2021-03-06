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
import com.dc.runbook.rt.cmd.IndividualCmdCancelRequest;
import com.dc.runbook.ssh.SshCommandExecutor;
import com.dc.runbook.ssh.SshCommandExecutorImpl;
import com.dc.util.batch.BatchUnitTask;

public class GroupCmdCancelTask implements BatchUnitTask {

    private IndividualCmdCancelRequest cancelRequest;

    public GroupCmdCancelTask(IndividualCmdCancelRequest cancelRequest) {
        this.cancelRequest = cancelRequest;
    }

    @Override
    public void execute() throws DcException {
        SshCommandExecutor sshCommandExecutor = new SshCommandExecutorImpl();
        sshCommandExecutor.cancel(cancelRequest);
    }
}