/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.ssh.utils;

import com.dc.ssh.client.SshException;

public class SshUtilsException extends SshException {

    public SshUtilsException(String message) {
        super(message);
    }

    public SshUtilsException(Throwable cause) {
        super(cause);
    }

    public SshUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshUtilsException(String message, boolean customized, String errorToken) {
        super(message, customized, errorToken);
    }

    public SshUtilsException(String message, Throwable cause, boolean customized, String errorToken) {
        super(message, cause, customized, errorToken);
    }
}
