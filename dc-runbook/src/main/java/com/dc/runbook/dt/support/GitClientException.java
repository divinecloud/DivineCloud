package com.dc.runbook.dt.support;

import com.dc.DcException;

public class GitClientException extends DcException {
    public GitClientException(String message) {
        super(message);
    }

    public GitClientException(Throwable cause) {
        super(cause);
    }

    public GitClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
