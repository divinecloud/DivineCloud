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

package com.dc.ssh.client.sftp;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.support.FileSupport;
import com.dc.ssh.client.support.SshSessionCreator;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import com.dc.ssh.client.test.support.TestSupport;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.jcraft.jsch.Session;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SftpClientTest {

    @Test
    public void testFileTransfer() {
        String sampleFileFolder = TestSupport.getProperty("ssh.sample.text.folder");
        File source = new File(sampleFileFolder, "sampleFile.txt");
        File destination = new File("/tmp/mySampleTextFile.txt");
        NodeCredentials credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        Session session = SshSessionCreator.getInstance().createSession(credentials, configuration);
        SftpClient sftpClient = new SftpClientImpl(session);
        SftpClientCallback callback = new SftpClientCallback();
        FileTransferCompletionTracker tracker = new FileTransferCompletionTracker(callback);
        tracker.start();
        sftpClient.putFile(source, destination.getAbsolutePath(), callback);
        try {
            tracker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        callback = new SftpClientCallback();
        tracker = new FileTransferCompletionTracker(callback);
        tracker.start();
        sftpClient.getFile(destination.getAbsolutePath(), destination, callback);
        try {
            tracker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(TestSupport.equals(FileSupport.readFile(source), FileSupport.readFile(destination)));

        SshSessionCreator.getInstance().disconnectSession(session, credentials);
    }

    private class SftpClientCallback implements SftpCallback {
        private volatile boolean done;
        private String execId;

        @Override
        public void done() {
            done = true;
            System.out.println(" Done for Exec ID : "  + execId);

        }

        @Override
        public void done(SftpClientException cause) {
            System.out.println(" Done for Exec ID : "  + execId);
            cause.printStackTrace();
            done = true;
        }

        @Override
        public int getStatusCode() {
            return 0;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public int precentageComplete(int count) {
            return 0;
        }

        @Override
        public void execId(String execId) {
            this.execId = execId;
            System.out.println("Exec ID : "  + execId);
        }

        @Override
        public SftpClientException getCause() {
            return null;
        }
    }

}
