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
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.sftp.stream.SftpClientReadStreamer;
import com.dc.ssh.client.sftp.stream.SftpClientReadStreamerBuilder;
import com.dc.ssh.client.sftp.stream.SftpClientWriteStreamer;
import com.dc.ssh.client.sftp.stream.SftpClientWriteStreamerBuilder;
import com.dc.ssh.client.support.FileSupport;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import com.dc.ssh.client.test.support.TestSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SftpClientStreamerTest {
    private NodeCredentials credentials;

    @Before
    public void before() {
        credentials = SshTestObjectsGenerator.createNodeCredentials();
    }

    @Test
    public void testReadAndWrite() {
        String sampleFileFolder = TestSupport.getProperty("ssh.sample.text.folder");
        String sampleFileName = TestSupport.getProperty("ssh.sample.text.file");
        String targetFolder = TestSupport.getProperty("ssh.sample.output.folder");
        File source = new File(sampleFileFolder, sampleFileName);
        String target = targetFolder + "/" + sampleFileName;

        byte[] sourceBytes = FileSupport.readFile(source);

        long writeStartTime = System.currentTimeMillis();
        writeBytes(sourceBytes, target);
        long writeEndTime = System.currentTimeMillis();
        System.out.println("Write Time = " + (writeEndTime - writeStartTime));

        long readStartTime = System.currentTimeMillis();
        byte[] receivedBytes = readBytes(target);
        long readEndTime = System.currentTimeMillis();
        System.out.println("Read Time = " + (readEndTime - readStartTime));
        assertTrue(TestSupport.equals(sourceBytes, receivedBytes));

        deleteFile(target);
    }

    private void writeBytes(byte[] sourceBytes, String target) {
        SftpClientWriteStreamer sftpClientWriteStreamer = SftpClientWriteStreamerBuilder.build(credentials, target);
        int part1Length = 10;
        int part2Length = sourceBytes.length - 10;
        byte[] part1 = new byte[part1Length];
        System.arraycopy(sourceBytes, 0, part1, 0, part1Length);
        byte[] part2 = new byte[part2Length];
        System.arraycopy(sourceBytes, part1Length, part2, 0, part2Length);
        sftpClientWriteStreamer.write(part1);
        sftpClientWriteStreamer.write(part2);
        sftpClientWriteStreamer.close();
    }

    private byte[] readBytes(String target) {
        SftpClientReadStreamer sftpClientReadStreamer = SftpClientReadStreamerBuilder.build(credentials, target);
        int bytesRead;
        byte[] buffer = new byte[1024];
        List<Byte> bytesList = new ArrayList<>();

        while((bytesRead = sftpClientReadStreamer.read(buffer)) >= 0) {
            copy(bytesList, buffer, bytesRead);
        }
        return convert(bytesList);
    }

    private void deleteFile(String target) {
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();

        SshClient client = SshClientBuilder.build(credentials, configuration);
        try {
            assertNotNull(target);
            ExecutionDetails result = client.execute("rm -rf " + target);
            assertNotNull(result);

        } catch (SshException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void copy(List<Byte> bytesList, byte[] buffer, int readCount) {
        for(int i=0; i<readCount; i++) {
            bytesList.add(buffer[i]);
        }
    }

    private byte[] convert(List<Byte> bytesList) {
        byte[] result = new byte[bytesList.size()];
        int currentIndex = 0;
        for(byte b : bytesList) {
            result[currentIndex++] = b;
        }
        return result;
    }

}
