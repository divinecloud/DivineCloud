package com.dc.ssh.client.sftp.stream;

import com.dc.ssh.client.sftp.SftpClientException;

public interface SftpClientTransferStreamer {

    /**
     * Transfers the file content between the local & the remote server.
     *
     * @param fileContent file content bytes
     * @throws com.dc.ssh.client.sftp.SftpClientException - Gets thrown for any SSH related issues.
     */
    public void transfer(byte[] fileContent) throws SftpClientException;

    /**
     * Closes the SFTP connection.
     *
     * @throws com.dc.ssh.client.sftp.SftpClientException - Gets thrown for any SSH related issues.
     */
    public void close() throws SftpClientException;

}
