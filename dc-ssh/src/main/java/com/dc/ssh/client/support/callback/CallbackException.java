package com.dc.ssh.client.support.callback;

import com.dc.DcException;

public class CallbackException extends DcException {

    private static final long serialVersionUID = 1L;

	public CallbackException(String message, Throwable cause) {
		super(message, cause);
	}

	public CallbackException(String message) {
		super(message);
	}

	public CallbackException(Throwable cause) {
		super(cause);
	}

}
