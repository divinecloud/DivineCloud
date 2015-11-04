package com.dc.ssh.client.sftp.stream;

import com.dc.ssh.client.sftp.SftpClientException;

public interface SftpClientReadStreamer extends SftpClientTransferStreamer {

    /**
     * Reads the file content from the remote server.
     *
     * @param buffer - byte array for data be read into.
     * @return total number of bytes read
     * @throws com.dc.ssh.client.sftp.SftpClientException - Gets thrown for any SSH related issues.
     */
    public int read(byte[] buffer) throws SftpClientException;
}
