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

package com.dc.ssh.client.sftp.stream;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.impl.SftpClientWriteStreamerImpl;
import com.dc.ssh.client.exec.vo.NodeCredentials;

public class SftpClientWriteStreamerBuilder {
    public static SftpClientWriteStreamer build(NodeCredentials credentials, String target) throws SftpClientException {
        SshClientConfiguration configuration   = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(40000).build();
        return new SftpClientWriteStreamerImpl(credentials, configuration, target);
    }
}
