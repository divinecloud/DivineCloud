package com.dc.ssh.utils;

import com.dc.ssh.client.SshException;

public class SshUtilsException extends SshException {

    public SshUtilsException(String message) {
        super(message);
    }

    public SshUtilsException(Throwable cause) {
        super(cause);
    }

    public SshUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshUtilsException(String message, boolean customized, String errorToken) {
        super(message, customized, errorToken);
    }

    public SshUtilsException(String message, Throwable cause, boolean customized, String errorToken) {
        super(message, cause, customized, errorToken);
    }
}
