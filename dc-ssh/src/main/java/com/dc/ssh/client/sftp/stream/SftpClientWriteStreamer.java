package com.dc.ssh.client.sftp.stream;

import com.dc.ssh.client.sftp.SftpClientException;

public interface SftpClientWriteStreamer extends SftpClientTransferStreamer {

    /**
     * Writes the file content to the remote server.
     *
     * @param fileContent file content bytes
     * @throws com.dc.ssh.client.sftp.SftpClientException - Gets thrown for any SSH related issues.
     */
    public void write(byte[] fileContent) throws SftpClientException;
}
