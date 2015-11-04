package com.dc;

public class DcException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private boolean customized;
    private String errorToken;

    public DcException(String message) {
        super(message);
    }

    public DcException(Throwable cause) {
        super(cause);
    }

    public DcException(String message, Throwable cause) {
        super(message, cause);
    }

    public DcException(String message, boolean customized, String errorToken) {
        super(message);
        this.customized = customized;
        this.errorToken = errorToken;
    }

    public DcException(String message, Throwable cause, boolean customized, String errorToken) {
        super(message, cause);
        this.customized = customized;
        this.errorToken = errorToken;
    }

    public boolean isCustomized() {
        return customized;
    }

    public String getErrorToken() {
        return errorToken;
    }
}
