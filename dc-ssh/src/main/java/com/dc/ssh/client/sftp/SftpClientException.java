package com.dc.ssh.client.sftp;

import com.dc.ssh.client.SshException;

public class SftpClientException extends SshException {

    public SftpClientException(String message) {
        super(message);
    }

    public SftpClientException(Throwable cause) {
        super(cause);
    }

    public SftpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
