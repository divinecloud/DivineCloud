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

package com.dc.api;

import com.dc.ssh.client.exec.vo.NodeCredentials;

public class NodeCredentialsGenerator {

    public static NodeCredentials generateServer1Credentials() {
        String host = TestSupport.getProperty("server1.host");
        String username = TestSupport.getProperty("server1.username");
        String password = TestSupport.getProperty("server1.password");
        return new NodeCredentials.Builder(host, username).password(password).id(host).build();
    }

    public static NodeCredentials generateServer2Credentials() {
        String host = TestSupport.getProperty("server2.host");
        String username = TestSupport.getProperty("server2.username");
        String keyText = TestSupport.getProperty("server2.key");
        return new NodeCredentials.Builder(host, username).id(host).privateKey(keyText.getBytes()).build();
    }

}
