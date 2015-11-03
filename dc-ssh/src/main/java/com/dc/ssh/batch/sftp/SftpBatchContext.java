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

import java.util.List;

public class SftpBatchContext {
    private SftpMode mode;
    private String executionId;
    private String from;
    private String to;
    private List<SshClient> sshClients;
    private SftpBatchCallback batchCallback;

    public SftpBatchContext(SftpMode mode, String executionId, String from, String to, List<SshClient> sshClients, SftpBatchCallback batchCallback) {
        this.mode = mode;
        this.executionId = executionId;
        this.from = from;
        this.to = to;
        this.sshClients = sshClients;
        this.batchCallback = batchCallback;
    }

    public SftpMode getMode() {
        return mode;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<SshClient> getSshClients() {
        return sshClients;
    }

    public SftpBatchCallback getBatchCallback() {
        return batchCallback;
    }
}
