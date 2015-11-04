package com.dc.support;

import com.dc.DcException;

public class SystemConfigException extends DcException {

    private static final long serialVersionUID = 1L;

    public SystemConfigException(String message) {
        super(message);
    }

    public SystemConfigException(Throwable cause) {
        super(cause);
    }

    public SystemConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
