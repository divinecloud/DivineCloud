package com.dc.ssh.client.shell;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.test.support.SshTestObjectsGenerator;
import org.junit.Test;

import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertTrue;

public class DirectConnectSshShellTest {

	@Test
	public void testSshShell() throws InterruptedException {
		NodeCredentials credentials;
		credentials = SshTestObjectsGenerator.createNodeCredentials();
		SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
		SampleOutputCallBack callback = new SampleOutputCallBack();

		SshShell shell = new DirectConnectSshShell(credentials, configuration, callback);
		shell.write("whoami\n".getBytes());
		shell.write("pwd\n".getBytes());
		shell.write("ps -ef\n".getBytes());
		Thread.sleep(2000);
		String result = new String(callback.getOutput());
		System.out.println(result);
		assertTrue(result.contains(SshTestObjectsGenerator.getUserName()));
		shell.close();
	}

	@Test
	public void testTopCommand() throws InterruptedException {
		NodeCredentials credentials;
		credentials = SshTestObjectsGenerator.createNodeCredentials();
		SshClientConfiguration configuration = new SshClientConfiguration.Builder().ptySupport(true).readTimeout(20000).build();
		SampleOutputCallBack callback = new SampleOutputCallBack();

		SshShell shell = new DirectConnectSshShell(credentials, configuration, callback);
		shell.write("top -n 2\n".getBytes());
		Thread.sleep(4000);
		String result = new String(callback.getOutput());
		System.out.println(result);
		assertTrue(result.contains(SshTestObjectsGenerator.getUserName()));
		shell.close();
	}

}

class SampleOutputCallBack implements OutputCallback {
	private final List<byte[]>	output;

	SampleOutputCallBack() {
		output = new Vector();
	}

	@Override
	public void output(byte[] bytes) {
		// System.out.println(new String(bytes));
		synchronized (output) {
			output.add(bytes);
		}
	}

	public byte[] getOutput() {
		byte[] outputBytes;
		synchronized (output) {
			int size = calcuateOutputSize();
			outputBytes = new byte[size];
			int i = 0;
			for (byte[] bytes : output) {
				for (byte b : bytes) {
					outputBytes[i++] = b;
				}
			}
		}
		return outputBytes;
	}

	private int calcuateOutputSize() {
		int result = 0;
		for (byte[] bytes : output) {
			result += bytes.length;
		}
		return result;
	}

	@Override
	public void done() {
		System.out.println("Done");
	}

	@Override
	public void error(String errorMessage) {
		System.out.println("Error occurred : " + errorMessage);
	}
}
