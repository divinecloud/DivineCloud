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

package com.dc.api.support;

import com.dc.DcException;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.util.batch.BatchUnitTask;

public class ConnectTask  implements BatchUnitTask {

    private String executionId;
    private SshClientAccessor sshClientAccessor;
    private NodeCredentials nodeCred;
    private volatile boolean success;

    public ConnectTask(String executionId, SshClientAccessor sshClientAccessor, NodeCredentials nodeCred) {
        this.executionId = executionId;
        this.sshClientAccessor = sshClientAccessor;
        this.nodeCred = nodeCred;
    }

    @Override
    public void execute() throws DcException {
        SshClient sshClient = sshClientAccessor.provide(nodeCred);
        if(sshClient != null) {
            success = true;
        }
    }

    public NodeCredentials getNodeCred() {
        return nodeCred;
    }

    public boolean isSuccess() {
        return success;
    }
}