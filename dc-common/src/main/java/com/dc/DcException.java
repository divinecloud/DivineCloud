/* *******************************************************************************
 *  Copyright 2011 Divine Cloud Inc.  All Rights Reserved.                       *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.                *
 *                                                                               *
 *  This code is property of Divine Cloud software (www.divinecloud.com),        *
 *  and cannot be used without valid license purchase.                           *
 *  Any part of code cannot be modified or distributed to others without the     *
 *  written permission from Divine Cloud.                                        *
 *                                                                               *
 *  This code is provided in the hope that it will benefit the user, but         *
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY   *
 *  or FITNESS FOR A PARTICULAR PURPOSE. Divine Cloud is not liable for any      *
 *  bugs in the software that can cause potential loss (monetarily or otherwise) *
 *  to the user.                                                                 *
 *                                                                               *
 *  Please contact Divine Cloud if you need additional information or have any   *
 *  questions.                                                                   *
 *********************************************************************************/

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
