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

package com.dc.ssh.client.support;

import com.dc.ssh.client.SshErrorTokens;
import com.dc.support.KeyValuePair;

public class SshExceptionParser {

    public static KeyValuePair<String,String> failedConnectionCause(Throwable e, String host) {
        KeyValuePair<String, String> pair = new KeyValuePair<>();
        String message;
        String errorToken;
        boolean unknownHost = false;
        boolean connectionRefused = false;
        boolean invalidCredentials = false;
        boolean invalidPort = false;
        boolean multiFactorCheckNeeded = false;
        boolean invalidConfiguration = false;
        boolean networkNotReachable = false;
        boolean serverNotReachable = false;
        boolean invalidPrivateKey = false;
        int depth = 5;
        Throwable t = e;
        do {
            if(t.getMessage() != null && (t.getMessage().contains("java.net.UnknownHostException") || t.getMessage().contains("socket is not established"))) {
                unknownHost = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("java.lang.IllegalArgumentException: port out of range")) {
                invalidPort = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("Connection refused")) {
                connectionRefused = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("Auth cancel")) {
                invalidCredentials = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("Auth fail")) {
                invalidCredentials = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("SSH_MSG_DISCONNECT") && t.getMessage().contains("Too many authentication failures")) {
                invalidCredentials = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("SocketException: Network is unreachable")) {
                networkNotReachable = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("channel is not opened")) {
                serverNotReachable = true;
                break;
            }
            else if(t.getMessage() != null && t.getMessage().contains("invalid privatekey")) {
                invalidPrivateKey = true;
                break;
            }
            else if(t instanceof NullPointerException) {
                invalidConfiguration = true;
                StackTraceElement[] elements = t.getStackTrace();
                for(StackTraceElement element : elements) {
                    if(element.getClassName().contains("UserAuthKeyboardInteractive")) {
                        multiFactorCheckNeeded = true;
                        invalidConfiguration = false;
                    }
                }
                if(multiFactorCheckNeeded) {
                    break;
                }
            }
            depth--;
        }while(depth > 0 && (t = e.getCause())!= null);

        if(unknownHost) {
            message = "Unknown Host Address : " + host;
            errorToken = SshErrorTokens.UNKNOWN_HOST;
        }
        else if(connectionRefused) {
            message = "Connection Refused for Host Address : " + host;
            errorToken = SshErrorTokens.CONNECTION_REFUSED;
        }
        else if(invalidCredentials) {
            message = "Invalid Credentials seem to be provided for Host Address : " + host;
            errorToken = SshErrorTokens.INVALID_CREDENTIALS;
        }
        else if(invalidPort) {
            message = "Invalid Port seem to be provided for Host Address : " + host;
            errorToken = SshErrorTokens.INVALID_PORT;
        }
        else if(multiFactorCheckNeeded) {
            message = "Multi-Factor Authentication need to be checked for Host : " + host;
            errorToken = SshErrorTokens.MULTI_FACTOR_AUTH_CHECK_NEEDED;
        }
        else if(invalidConfiguration) {
            message = "Potential invalid Configuration provided for Host : " + host;
            errorToken = SshErrorTokens.INVALID_NODE_CONFIG;
        }
        else if(networkNotReachable) {
            message = "Network Not Reachable for Host : " + host;
            errorToken = SshErrorTokens.NETWORK_UNREACHABLE;
        }
        else if(serverNotReachable) {
            message = "Host : " + host + " NOT Reachable";
            errorToken = SshErrorTokens.SERVER_UNREACHABLE;
        }
        else if(invalidPrivateKey) {
            message = "Host : " + host + " Invalid Private Key Credentials Provided.";
            errorToken = SshErrorTokens.INVALID_PRIVATE_KEY;
        }
        else {
            message = "Unable to connect to Host : " + host;
            errorToken = SshErrorTokens.GENERIC_CONNECT_ERROR;
            e.printStackTrace();
        }
        pair.setKey(errorToken);
        pair.setValue(message);
        return pair;
    }
}
