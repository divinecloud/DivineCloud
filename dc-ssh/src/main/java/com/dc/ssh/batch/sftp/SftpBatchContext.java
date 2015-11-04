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
