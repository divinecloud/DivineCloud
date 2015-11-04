package com.dc.ssh.client.async;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.sftp.FileTransferCompletionTracker;
import com.dc.ssh.client.sftp.SftpCallback;
import com.dc.ssh.client.sftp.SftpClient;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.support.FileSupport;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import com.dc.ssh.client.test.support.TestSupport;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class SshClientFileTransferTest {

    @Test
    public void testFileTransfer() {
        String sampleFileFolder = TestSupport.getProperty("ssh.sample.text.folder");
        File source = new File(sampleFileFolder, "sampleFile.txt");
        String destinationPath = "/tmp/mySampleTextFile.txt";
        File destination = new File("/tmp/mySampleTextFile.txt");


        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            SftpClient sftpClient = sshClient.getSftpClient();
            SftpClientCallback callback = new SftpClientCallback();
            FileTransferCompletionTracker tracker = new FileTransferCompletionTracker(callback);
            tracker.start();
            sftpClient.putFile(source.getAbsoluteFile(), destinationPath, callback);
            try {
                tracker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

            callback = new SftpClientCallback();
            tracker = new FileTransferCompletionTracker(callback);
            tracker.start();
            sftpClient.getFile(destinationPath, destination, callback);
            try {
                tracker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
            assertTrue(TestSupport.equals(FileSupport.readFile(source), FileSupport.readFile(destination)));
            ExecutionDetails deletedFileResults = sshClient.execute("rm -f " + destination);
            assertNotNull(deletedFileResults);
            assertTrue(deletedFileResults.getStatusCode() == 0);

        } catch (Throwable e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

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
