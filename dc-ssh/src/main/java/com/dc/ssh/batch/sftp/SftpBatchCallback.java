package com.dc.ssh.batch.sftp;

import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.support.KeyValuePair;

import java.util.List;

public interface SftpBatchCallback {

    public String executionId();

    public void completeForNode(String displayId);

    public void completeForNode(String displayId, SftpClientException cause);

    public void done();

    public void failed(List<KeyValuePair<String, String>> failedNodesList);

    public void cancelled();

    public boolean isDone();

    public void percentageCompleteForNode(String displayId, int percent);

    public SftpClientException getCause();
}
