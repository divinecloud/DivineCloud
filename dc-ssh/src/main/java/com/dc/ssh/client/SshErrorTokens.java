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


public class SshErrorTokens {
    public static final String GENERIC_CONNECT_ERROR = "GENERIC_CONNECT_ERROR";
    public static final String GENERIC_EXECUTE_ERROR = "GENERIC_EXECUTE_ERROR";
    public static final String GENERIC_READ_WRITE_ERROR = "GENERIC_READ_WRITE_ERROR";
    public static final String CANCEL_FAILED = "CANCEL_FAILED";
    public static final String NETWORK_UNREACHABLE = "NETWORK_UNREACHABLE";
    public static final String SERVER_UNREACHABLE = "SERVER_UNREACHABLE";
    public static final String UNKNOWN_HOST = "UNKNOWN_HOST";
    public static final String CONNECTION_REFUSED = "CONNECTION_REFUSED";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String INVALID_PORT = "INVALID_PORT";
    public static final String CHANNEL_CANNOT_OPEN = "CHANNEL_CANNOT_OPEN";
    public static final String SLEEP_INTERRUPTED = "SLEEP_INTERRUPTED";
    public static final String MULTI_FACTOR_AUTH_CHECK_NEEDED = "MULTI_FACTOR_AUTH_CHECK_NEEDED";
    public static final String INVALID_NODE_CONFIG = "INVALID_NODE_CONFIG";
    public static final String INVALID_PRIVATE_KEY = "INVALID_PRIVATE_KEY";

    public static final String INVALID_ARGS = "INVALID_ARGS";
    public static final String CANNOT_CREATE_DIR = "CANNOT_CREATE_DIR";
}
