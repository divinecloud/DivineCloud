package com.dc;

/**
 * Gets thrown for any user made errors.
 */
public class DcUserException extends DcException {

    public DcUserException(String message) {
        super(message);
    }
}
