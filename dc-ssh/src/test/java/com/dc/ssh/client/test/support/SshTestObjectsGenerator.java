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

package com.dc.ssh.client.test.support;

import com.dc.ssh.client.exec.vo.NodeCredentials;

public class SshTestObjectsGenerator {

    public static boolean isKeyUsed() {
        boolean useKey = false;
        String useKeyStr = TestSupport.getProperty("ssh.client.usekey");
        if(useKeyStr.equalsIgnoreCase("true")) {
            useKey = true;
        }
        return useKey;
    }

    public static String getUserName() {
        String userName;
        if(isKeyUsed()) {
            userName = TestSupport.getProperty("ssh.client.key.type.username");
        }
        else {
            userName = TestSupport.getProperty("ssh.client.pass.type.username");
        }
        return userName;
    }



    public static NodeCredentials createNodeCredentials() {
        boolean useKey = isKeyUsed();
        NodeCredentials credentials;
        if(useKey) {
            credentials = createNodeCredentialsWithKey(TestSupport.getProperty("ssh.client.key.type.username"), TestSupport.getProperty("ssh.client.key.type.host"));
        }
        else {
            credentials = createNodeCredentialsWithPassword(TestSupport.getProperty("ssh.client.pass.type.username"), TestSupport.getProperty("ssh.client.pass.type.host"));
        }
        return credentials;
    }

    public static NodeCredentials createNodeCredentialsWithPassphrase() {
        NodeCredentials credentials;
        credentials = createNodeCredentialsWithKeyNPassphrase(TestSupport.getProperty("ssh.client.key.with.phrase.username"), TestSupport.getProperty("ssh.client.key.with.phrase.host"));
        return credentials;
    }

    public static NodeCredentials createNodeCredentialsWithLocalCacheSupport() {
        boolean useKey = isKeyUsed();
        NodeCredentials credentials;
        if(useKey) {
            credentials = createNodeCredentialsWithKey(TestSupport.getProperty("ssh.client.key.type.username"), TestSupport.getProperty("ssh.client.key.type.host"),
                    TestSupport.getProperty("ssh.client2.key.type.cachedFilePath"));
        }
        else {
            credentials = createNodeCredentialsWithPassword(TestSupport.getProperty("ssh.client.pass.type.username"), TestSupport.getProperty("ssh.client.pass.type.host"),
                    TestSupport.getProperty("ssh.client2.key.type.cachedFilePath"));
        }
        return credentials;
    }

    public static NodeCredentials createInvalidNameNodeCredentials() {
        boolean useKey = isKeyUsed();
        NodeCredentials credentials;
        if(useKey) {
            credentials = createNodeCredentialsWithKey("wronguser", TestSupport.getProperty("ssh.client.key.type.host"));
        }
        else {
            credentials = createNodeCredentialsWithPassword("wronguser", TestSupport.getProperty("ssh.client.pass.type.host"));
        }
        return credentials;
    }


    public static NodeCredentials createInvalidPortNodeCredentials() {
        boolean useKey = isKeyUsed();
        NodeCredentials credentials;
        if(useKey) {
            credentials = createNodeCredentialsWithKey("wronguser", TestSupport.getProperty("ssh.client.key.type.host"), 666666);
        }
        else {
            credentials = createNodeCredentialsWithPassword("wronguser", TestSupport.getProperty("ssh.client.pass.type.host"), 666666);
        }

        return credentials;
    }

    public static NodeCredentials createInvalidHostNodeCredentials() {
        boolean useKey = isKeyUsed();
        NodeCredentials credentials;
        if(useKey) {
            credentials = createNodeCredentialsWithKey(TestSupport.getProperty("ssh.client.key.type.username"), "192.168.1.190"); //invalid IP
        }
        else {
            credentials = createNodeCredentialsWithPassword(TestSupport.getProperty("ssh.client.pass.type.username"), "192.168.1.190"); //invalid IP
        }
        return credentials;
    }

    private static NodeCredentials createNodeCredentialsWithPassword(String userName, String host) {
        return new NodeCredentials.Builder(host, userName)
                .password(TestSupport.getProperty("ssh.client.pass.type.password")).build();
    }

    private static NodeCredentials createNodeCredentialsWithKey(String userName, String host) {
        return new NodeCredentials.Builder(host, userName)
                .privateKey(TestSupport.getProperty("ssh.client.key.type.privatekey").getBytes())
                .build();
    }

    private static NodeCredentials createNodeCredentialsWithKeyNPassphrase(String userName, String host) {
        return new NodeCredentials.Builder(host, userName)
                .privateKey(TestSupport.getProperty("ssh.client.key.with.phrase.privatekey").getBytes())
                .passPhrase(TestSupport.getProperty("ssh.client.key.with.phrase.passphrase"))
                .build();
    }

    private static NodeCredentials createNodeCredentialsWithPassword(String userName, String host, String cachedFilesPath) {
        return new NodeCredentials.Builder(host, userName)
                .password(TestSupport.getProperty("ssh.client.pass.type.password")).build();
    }

    private static NodeCredentials createNodeCredentialsWithKey(String userName, String host, String cachedFilesPath) {
        return new NodeCredentials.Builder(host, userName)
                .privateKey(TestSupport.getProperty("ssh.client.key.type.privatekey").getBytes()).build();
    }

    private static NodeCredentials createNodeCredentialsWithKey(String userName, String host, int port) {
        return new NodeCredentials.Builder(host, userName)
                .privateKey(TestSupport.getProperty("ssh.client.key.type.privatekey").getBytes())
                .port(port)
                .build();
    }

    private static NodeCredentials createNodeCredentialsWithPassword(String userName, String host, int port) {
        return new NodeCredentials.Builder(host, userName)
                .port(port)
                .password(TestSupport.getProperty("ssh.client.pass.type.password")).build();
    }


}