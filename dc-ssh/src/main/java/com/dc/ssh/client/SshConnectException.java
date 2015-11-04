package com.dc.ssh.client;

/**
 * Gets thrown for any SSH connection related issues.
 */
public class SshConnectException extends SshException {

    public SshConnectException(String message) {
        super(message);
    }

    public SshConnectException(Throwable cause) {
        super(cause);
    }

    public SshConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshConnectException(String message, boolean customized, String errorToken) {
        super(message, customized, errorToken);
    }

    public SshConnectException(String message, Throwable cause, boolean customized, String errorToken) {
        super(message, cause, customized, errorToken);
    }
}
