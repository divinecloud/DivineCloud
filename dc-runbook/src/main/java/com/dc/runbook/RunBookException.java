package com.dc.runbook;

import com.dc.DcException;

public class RunBookException extends DcException {

    public RunBookException(String message) {
        super(message);
    }

    public RunBookException(Throwable cause) {
        super(cause);
    }

    public RunBookException(String message, Throwable cause) {
        super(message, cause);
    }
}
