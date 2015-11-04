package com.dc.ssh.client.async;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshErrorTokens;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.RunAsAttributes;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.cmd.SshCommandAttributes;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.CommandCompletionTracker;
import com.dc.ssh.client.test.support.SampleCallback;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for single ssh command execution.
 */
public class SshClientSingleCommandTest {
    @Test
    public void testSingleCmdExecute() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "whoami");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            long startTime = System.currentTimeMillis();
            sshClient.execute(command, callback);
            tracker.join();
            long endTime = System.currentTimeMillis();
            System.out.println("Total time : " + (endTime - startTime) + " start : " + startTime + " end : " + endTime);

            assertEquals(0, callback.getStatusCode());
            assertEquals(SshTestObjectsGenerator.getUserName(), new String(callback.getOutput()).trim());
            System.out.println(new String(callback.getOutput()));
            //Thread.sleep(999999999);

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    @Test
    public void testSingleCmdExecuteAsDifferentUser() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            RunAsAttributes runAsAttributes = new RunAsAttributes("dcuser", "D1v1n3Cl0ud", false);
            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), runAsAttributes, null, false);
            SingleSshCommand command = new SingleSshCommand(cmdAttributes, "whoami");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));
            System.out.println(new String(callback.getError()));
            assertEquals(0, callback.getStatusCode());
            assertTrue(new String(callback.getOutput()).contains("dcuser"));
            assertTrue(new String(callback.getOutput()).contains("D1v1n3Cl0ud"));

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testSingleCmdExecuteAsDifferentUserWithNoPasswordEchoBack() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            RunAsAttributes runAsAttributes = new RunAsAttributes("dcuser", "D1v1n3Cl0ud", false);
            SshCommandAttributes cmdAttributes = new SshCommandAttributes("ID_TEST_" + System.currentTimeMillis(), runAsAttributes, null, false);
            SingleSshCommand command = new SingleSshCommand(cmdAttributes, "whoami");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            sshClient.execute(command, callback);
            tracker.join();
            System.out.println(new String(callback.getOutput()));
            System.out.println(new String(callback.getError()));
            assertEquals(0, callback.getStatusCode());
            assertTrue(new String(callback.getOutput()).contains("dcuser"));
            assertFalse(new String(callback.getOutput()).contains("D1v1n3Cl0ud"));


        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testInvalidPort() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createInvalidPortNodeCredentials();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "whoami");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            long startTime = System.currentTimeMillis();
            sshClient.execute(command, callback);
            tracker.join();
            long endTime = System.currentTimeMillis();
            System.out.println("Total time : " + (endTime - startTime) + " start : " + startTime + " end : " + endTime);

            assertEquals(0, callback.getStatusCode());
            assertEquals(SshTestObjectsGenerator.getUserName() + "\r\n", new String(callback.getOutput()));
            System.out.println(new String(callback.getOutput()));
            //Thread.sleep(999999999);

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (SshException e) {
            assertEquals(SshErrorTokens.INVALID_PORT, e.getErrorToken());
            //e.printStackTrace();
        }

    }

    @Test
    public void testSingleCmdExecuteWithPassphraseSupport() {
        NodeCredentials credentials;
        credentials = SshTestObjectsGenerator.createNodeCredentialsWithPassphrase();
        SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
        try(SshClient sshClient = SshClientBuilder.build(credentials, configuration)) {
            SingleSshCommand command = new SingleSshCommand("ID_TEST_" + System.currentTimeMillis(), "whoami");
            SampleCallback callback = new SampleCallback();
            CommandCompletionTracker tracker = new CommandCompletionTracker(callback);
            tracker.start();
            long startTime = System.currentTimeMillis();
            sshClient.execute(command, callback);
            tracker.join();
            long endTime = System.currentTimeMillis();
            System.out.println("Total time : " + (endTime - startTime) + " start : " + startTime + " end : " + endTime);

            assertEquals(0, callback.getStatusCode());
            assertEquals("ec2-user" + "\r\n", new String(callback.getOutput()));
            System.out.println(new String(callback.getOutput()));
            //Thread.sleep(999999999);

        } catch (SshException | InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
