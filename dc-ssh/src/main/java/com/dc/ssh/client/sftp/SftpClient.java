package com.dc.ssh.client.sftp;

import java.io.File;

public interface SftpClient {

    public String putFile(File source, String target, SftpCallback callback) throws SftpClientException;

    public String putFile(byte[] data, String target, SftpCallback callback) throws SftpClientException;

    public String getFile(String source, File target, SftpCallback callback) throws SftpClientException;

    public byte[] getFile(String source) throws SftpClientException;

    public void cancel(String execId);
}
