/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
