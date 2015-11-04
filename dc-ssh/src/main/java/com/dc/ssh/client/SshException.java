package com.dc.ssh.client;

import com.dc.DcException;

/**
 * Top-level exception thrown for any ssh related issues.
 */

public class SshException extends DcException {

    public SshException(String message) {
        super(message);
    }

    public SshException(Throwable cause) {
        super(cause);
    }

    public SshException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshException(String message, boolean customized, String errorToken) {
        super(message, customized, errorToken);
    }

    public SshException(String message, Throwable cause, boolean customized, String errorToken) {
        super(message, cause, customized, errorToken);
    }
}
