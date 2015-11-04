package com.dc.ssh.batch.sftp;


import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import com.dc.support.KeyValuePair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SftpBatchExecutorImplTest {

    @Test
    public void testBatchUploadExecute() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        SshClient sshClient = SshClientBuilder.build(credentials, configuration);
        List<SshClient> list = new ArrayList<>();
        list.add(sshClient);

        SftpBatchContext context = new SftpBatchContext(SftpMode.UPLOAD, "sample_exec_id_1", "/Users/bhupen/T/sample_out.txt", "/tmp/sample_out.txt", list, new SampleSftpBatchCallback());
        SftpBatchExecutorImpl batchExecutor = new SftpBatchExecutorImpl(context, 10);
        batchExecutor.executeBatch();
        try {
            Thread.sleep(10000);
            sshClient.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBatchDownloadExecute() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        SshClient sshClient = SshClientBuilder.build(credentials, configuration);
        List<SshClient> list = new ArrayList<>();
        list.add(sshClient);

        SftpBatchContext context = new SftpBatchContext(SftpMode.DOWNLOAD, "sample_exec_id_1", "/tmp/sample_out.txt", "/Users/bhupen/T/s1out.txt", list, new SampleSftpBatchCallback());
        SftpBatchExecutorImpl batchExecutor = new SftpBatchExecutorImpl(context, 10);
        batchExecutor.executeBatch();
        try {
            Thread.sleep(5000);
            sshClient.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBatchUploadCancel() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        SshClient sshClient = SshClientBuilder.build(credentials, configuration);
        List<SshClient> list = new ArrayList<>();
        list.add(sshClient);

        SftpBatchContext context = new SftpBatchContext(SftpMode.UPLOAD, "sample_exec_id_1", "/Users/bhupen/T/large_file.txt", "/tmp/large_file.txt", list, new SampleSftpBatchCallback());
        SftpBatchExecutorImpl batchExecutor = new SftpBatchExecutorImpl(context, 10);
        batchExecutor.executeBatch();
        try {
            Thread.sleep(15000);
            batchExecutor.cancel();
            Thread.sleep(2000);
            sshClient.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testBatchDownloadCancel() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        SshClient sshClient = SshClientBuilder.build(credentials, configuration);
        List<SshClient> list = new ArrayList<>();
        list.add(sshClient);

        SftpBatchContext context = new SftpBatchContext(SftpMode.DOWNLOAD, "sample_exec_id_1", "/tmp/large_file.txt", "/Users/bhupen/T/s1LargeFile.txt", list, new SampleSftpBatchCallback());
        SftpBatchExecutorImpl batchExecutor = new SftpBatchExecutorImpl(context, 10);
        batchExecutor.executeBatch();
        try {
            Thread.sleep(2000);
            batchExecutor.cancel();
            Thread.sleep(2000);
            sshClient.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}


class SampleSftpBatchCallback implements SftpBatchCallback {
    @Override
    public String executionId() {
        return null;
    }

    @Override
    public void completeForNode(String displayId) {
        System.out.println("displayId = " + displayId);
    }

    @Override
    public void failed(List<KeyValuePair<String, String>> failedNodesList) {
        System.out.println("failedNodesList = " + failedNodesList);
    }

    @Override
    public void completeForNode(String displayId, SftpClientException cause) {
        System.out.println("displayId = " + displayId);
        cause.printStackTrace();
    }

    @Override
    public void done() {
        System.out.println("Batch Execution Done");
    }

    @Override
    public void cancelled() {
        System.out.println("Execution Cancelled");
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void percentageCompleteForNode(String displayId, int percent) {
        System.out.println("displayId = " + displayId + " percent = " + percent);
    }

    @Override
    public SftpClientException getCause() {
        return null;
    }
}