package com.dc.ssh.batch.sftp;

import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.sftp.stream.SftpStreamer;
import com.dc.support.KeyValuePair;

import java.util.List;

public interface ConnectCompleteNotification {
    public void connectionComplete(String executionId, List<KeyValuePair<SshClient, SftpStreamer>> list, List<KeyValuePair<String, String>> failedList);
}
