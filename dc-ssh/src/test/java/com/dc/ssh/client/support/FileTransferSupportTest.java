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

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import com.jcraft.jsch.Session;
import org.junit.Test;

import java.io.File;

import static com.dc.ssh.client.support.SshClientConstants.SSH_SCRIPT_PERMS;

public class FileTransferSupportTest {

    @Test
    public void testFileTransfer() {

        String destination;
        byte[] sourceBytes = null;
        String source = "/Users/bhupen/T/large_file.txt";
        sourceBytes = FileSupport.readFile(new File(source));

        destination = "/tmp/lfile.txt";


        NodeCredentials credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        Session session = SshSessionCreator.getInstance().createSession(credentials, configuration);
        long startTime = System.currentTimeMillis();
        FileTransferSupport.transfer(session, sourceBytes, destination, SSH_SCRIPT_PERMS);
        long endTime = System.currentTimeMillis();
        System.out.println("Time = " + (endTime - startTime));
        session.disconnect();
    }

}
