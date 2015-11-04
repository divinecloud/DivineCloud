package com.dc.support;

import com.dc.DcException;

public class UserInputException extends DcException {

	private static final long	serialVersionUID	= 1L;

	public UserInputException(String message) {
		super(message);
	}

	public UserInputException(Throwable cause) {
		super(cause);
	}

	public UserInputException(String message, Throwable cause) {
		super(message, cause);
	}
}
