package com.dc.ssh.batch.sftp;

import com.dc.ssh.client.sftp.SftpClientException;

public interface SftpBatchExecutor {

    public void executeBatch() throws SftpClientException;

    public void cancel() throws SftpClientException;
}
