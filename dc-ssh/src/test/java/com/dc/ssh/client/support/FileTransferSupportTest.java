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
