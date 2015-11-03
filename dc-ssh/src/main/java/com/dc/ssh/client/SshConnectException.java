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

package com.dc.ssh.client;

/**
 * Gets thrown for any SSH connection related issues.
 */
public class SshConnectException extends SshException {

    public SshConnectException(String message) {
        super(message);
    }

    public SshConnectException(Throwable cause) {
        super(cause);
    }

    public SshConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshConnectException(String message, boolean customized, String errorToken) {
        super(message, customized, errorToken);
    }

    public SshConnectException(String message, Throwable cause, boolean customized, String errorToken) {
        super(message, cause, customized, errorToken);
    }
}
