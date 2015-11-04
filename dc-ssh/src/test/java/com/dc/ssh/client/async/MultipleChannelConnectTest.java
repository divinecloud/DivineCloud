package com.dc.ssh.client.async;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.support.ThreadPoolConfiguration;
import com.dc.ssh.client.test.support.CommandCompletionTracker;
import com.dc.ssh.client.test.support.SampleCallback;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MultipleChannelConnectTest {

    @Test
    public void testMultipleChannelConnections() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration(8, 50, 120);
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).threadPool(threadPoolConfiguration).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            List<ChannelWorker> workers = new ArrayList<>();
            for(int i=0; i<9; i++) {
                workers.add(new ChannelWorker(sshClient));
                workers.get(i).start();
            }
            for(int i=0; i<9; i++) {
                workers.get(i).join();
            }
            Thread.sleep(2000);
        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

    class ChannelWorker extends Thread {
        private SshClient sshClient;
        private volatile boolean success;

        public ChannelWorker(SshClient sshClient) {
            this.sshClient = sshClient;
        }

        public boolean isSuccess() {
            return success;
        }

        public void run() {
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "whoami; sleep 3; id");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            try {
                tracker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                success = false;
            }
            assertEquals(0, callback.getStatusCode());
            assertTrue(new String(callback.getOutput()).contains(SshTestObjectsGenerator.getUserName()));
            System.out.println("OUTPUT: " + new String(callback.getOutput()));
            System.out.println("ERROR: " + new String(callback.getError()));
        }

    }
}

