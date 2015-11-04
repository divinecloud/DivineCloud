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

package com.dc.ssh.client.builder;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.SshClientImpl;
import com.dc.ssh.client.exec.vo.NodeCredentials;

/**
 * Builder for creating Ssh client builder.
 */
public class SshClientBuilder {

    public static SshClient build(NodeCredentials nodeCredentials, SshClientConfiguration configuration) throws SshException {
        long startTime = System.currentTimeMillis();
        SshClient client = new SshClientImpl(nodeCredentials, configuration);
        long endTime = System.currentTimeMillis();
        System.out.println("Total Connect Time : " + (endTime - startTime));
        return client;
    }
}
