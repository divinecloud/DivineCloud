/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.ssh.client.shell;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshConnectException;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.support.SshExceptionParser;
import com.dc.ssh.client.support.callback.CallbackException;
import com.dc.support.KeyValuePair;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JumpHostConnectSshShell extends AbstractSshClient implements SshShell {

	private final int	           id;
	private final OutputCallback	callback;
	private Channel	               channel;
	private OutputStream	       channelOutStream;
	private InputStream	           inputStream;
	private volatile boolean	   done;
	private ShellOutputReader	   readerThread;

	private String	               host;

	public JumpHostConnectSshShell(List<NodeConfig> nodeConfigList, SshClientConfiguration configuration, OutputCallback callback) {
		super(nodeConfigList, configuration);
		AtomicInteger counter = new AtomicInteger();
		this.callback = callback;
		this.nodeConfigList = nodeConfigList;
		sessions = new Session[nodeConfigList.size()];
		connect();
		id = counter.incrementAndGet();
		readerThread = new ShellOutputReader(id);
		readerThread.start();
		host = nodeConfigList.get(nodeConfigList.size() - 1).getNodeCredentials().getHost();
	}

	private void connect() {
//		createSession();
		createChannel();
	}

	private void createChannel() {
		try {
			channel = currentSession.openChannel("shell");
			((ChannelShell) channel).setAgentForwarding(true);
			((ChannelShell) channel).setPtyType("vt102");
			channelOutStream = channel.getOutputStream();
			channel.connect();
			inputStream = channel.getInputStream();
		} catch (Throwable e) {
            KeyValuePair<String, String> pair = SshExceptionParser.failedConnectionCause(e, host);
			throw new SshConnectException(pair.getValue(), e, true, pair.getKey());
		}
	}


	@Override
	public void close() {
		done = true;
		readerThread.interrupt();
		super.close();
	}

	@Override
	public String getId() {
		return host + " - " + id;
	}

	@Override
	public boolean isConnected() {
		return currentSession.isConnected();
	}

	@Override
	public OutputCallback getCallback() {
		return callback;
	}

	@Override
	public void write(byte[] bytes) throws SshException {
		synchronized (this) {
			if (!currentSession.isConnected() || !channel.isConnected()) {
				try {
					reconnect();
				} catch (IOException | JSchException e) {
					throw new SshException(e);
				}
			}
			if (readerThread.sleepInterval() > 1000) {
				readerThread.setWakeUp(true);
				readerThread.interrupt();
			}
		}

		try {
			channelOutStream.write(bytes);
			channelOutStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SshException(e);
		}
	}

	@Override
	public void resize(int col, int row, int wp, int hp) {
		synchronized (this) {
			((ChannelShell) channel).setPtySize(col, row, wp, hp);
		}
	}

	private void reconnect() throws IOException, JSchException {
		if (!currentSession.isConnected()) {
			createSession();
			createChannel();
		}
	}

	private long sleepConditionally(long sleepTime, InputStream in) throws IOException, InterruptedException {
		if (!(in.available() > 0)) {
			sleepTime += 10;
			Thread.sleep(sleepTime);
		}
		if (sleepTime > configuration.getReadLatency()) {
			sleepTime = 30000;
		}
		return sleepTime;
	}

	private class ShellOutputReader extends Thread {
		private volatile boolean	complete;
		private int		         outputReaderId;
		private volatile boolean	wakeUp;
		private AtomicLong		 sleepTime;

		private ShellOutputReader(int outputReaderId) {
			this.outputReaderId = outputReaderId;
		}

		public int getOutputReaderId() {
			return outputReaderId;
		}

		public boolean isComplete() {
			return complete;
		}

		public void setWakeUp(boolean wakeUp) {
			this.wakeUp = wakeUp;
		}

		public long sleepInterval() {
			return sleepTime.get();
		}

		public void run() {
			InputStream in = null;
			byte[] tmp = new byte[configuration.getBufferSize()];
			sleepTime = new AtomicLong();
			long totalSleepTime = 0;
			/* long timeoutThreshold = getTimeoutThreshold(configuration); */
			int i;
			byte[] result;

			try {
				while (!done /* && (totalSleepTime < timeoutThreshold) */) {
					try {
						synchronized (this) {
							in = inputStream;
						}
						if (in.available() > 0) {
							i = in.read(tmp, 0, configuration.getBufferSize());
							result = new byte[i];
							System.arraycopy(tmp, 0, result, 0, i);
							callback.output(result);
							sleepTime.set(0);
							totalSleepTime = 0;
						} else {
							long sleptFor = sleepConditionally(sleepTime.get(), in);
							sleepTime.set(sleptFor);
							totalSleepTime += sleptFor;
						}
					} catch (InterruptedException e) {
						if (wakeUp) {
							wakeUp = false;
							sleepTime.set(0);
							totalSleepTime = 0;
						}
						if (done) {
							return;
						}
						// Ignore this exception, as it is expected when user
						// initiates stop request for the thread.
						// e.printStackTrace();
					} catch (CallbackException e) {
						// Callback failed, close and move on
						e.printStackTrace();
						callback.error(e.getMessage());
					} catch (IOException e) {
						e.printStackTrace();
						callback.error(e.getMessage());
					} catch (Throwable e) {
						e.printStackTrace();
						callback.error(e.getMessage());
					}
				}
			} finally {
				complete = true;
				if (done) {
					callback.done();
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							callback.error(e.getMessage());
						}
					}
				}
			}
		}
	}

}
