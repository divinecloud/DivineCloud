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

package com.dc.ssh.client.shell;

import com.dc.ssh.client.SshConnectException;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.support.SshSessionCreator;
import com.dc.ssh.client.support.callback.CallbackException;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class DirectConnectSshShell implements SshShell {
    private final OutputCallback callback;
    private final NodeCredentials nodeCredentials;
    private final SshClientConfiguration configuration;
    private final AtomicReference<Session> sessionReference;
    private final AtomicReference<Channel> channelReference;
    private final AtomicReference<OutputStream> channelOutStreamReference;
    private ShellOutputReader readerThread;
    private volatile boolean done;
    private static AtomicInteger counter = new AtomicInteger();
    private AtomicReference<InputStream> inputStreamReference;
    private int id;

    public DirectConnectSshShell(NodeCredentials nodeCredentials, SshClientConfiguration configuration, OutputCallback callback) {
        this.callback = callback;
        this.nodeCredentials = nodeCredentials;
        this.configuration = configuration;
        sessionReference = new AtomicReference<>();
        channelReference = new AtomicReference<>();
        channelOutStreamReference = new AtomicReference<>();
        inputStreamReference = new AtomicReference<>();
        createSession();
        try {
            createChannel();
            System.out.println("Channel Connect status : " + channelReference.get().isConnected());
        } catch (JSchException | IOException e) {
            throw new SshConnectException(e);
        }
        id = counter.incrementAndGet();
        readerThread = new ShellOutputReader(id);
        readerThread.start();
        System.out.println(System.currentTimeMillis() + " - Node : " + nodeCredentials.getHost() + " - SSH Shell Connected. Thread ID : " + readerThread.getOutputReaderId());

    }

    public void close() {
        System.out.println("SSH Shell close() called for ID : " + readerThread.getOutputReaderId());
        done = true;
        readerThread.interrupt();
        sessionReference.get().disconnect();
    }

    public String getId() {
        return nodeCredentials.getHost() + " - " + id;
    }

    public boolean isConnected() {
        return sessionReference.get().isConnected();
    }

    public OutputCallback getCallback() {
        return callback;
    }

    public void write(byte[] bytes) {
        synchronized (sessionReference) {
            if (!sessionReference.get().isConnected() || !channelReference.get().isConnected()) {
                System.out.println(System.currentTimeMillis() + " - session connection closed - " + readerThread.getOutputReaderId());
                try {
                    reconnect();
                } catch (IOException | JSchException e) {
                    throw new SshException(e);
                }
//            } else if (readerThread.isComplete() && !done) {
//                readerThread.interrupt();
//                readerThread = new ShellOutputReader(counter.incrementAndGet());
//                readerThread.start();
//                System.out.println(System.currentTimeMillis() + " - Node : " + nodeCredentials.getHost() + " SSH Shell Thread re-created. New Thread ID : " + readerThread.getOutputReaderId());
//
            }
            if (readerThread.sleepInterval() > 1000) {
                readerThread.setWakeUp(true);
                readerThread.interrupt();
            }
        }


        try {
            channelOutStreamReference.get().write(bytes);
            channelOutStreamReference.get().flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SshException(e);
        }
    }

    public void resize(int col, int row, int wp, int hp) {
        synchronized (sessionReference) {
            ((ChannelShell) channelReference.get()).setPtySize(col, row, wp, hp);
        }
    }

    private void reconnect() throws IOException, JSchException {
        System.out.println(System.currentTimeMillis() + " |- Reconnecting - " + getId());
        if (!sessionReference.get().isConnected()) {
            createSession();
            createChannel();
        }
//        if (!channelReference.get().isConnected()) {
//            createChannel();
//        }
//        readerThread.interrupt();
//        readerThread = new ShellOutputReader(counter.incrementAndGet());
//        readerThread.start();
        System.out.println(System.currentTimeMillis() + " |-- Reconnected - " + getId());
        System.out.println(System.currentTimeMillis() + " - Node : " + nodeCredentials.getHost() + " SSH Shell Reconnect complete. Thread ID : " + readerThread.getOutputReaderId());
    }

    private void createSession() {
        sessionReference.set(SshSessionCreator.getInstance().createSession(nodeCredentials, configuration));
    }

    private void createChannel() throws JSchException, IOException {
        channelReference.set(sessionReference.get().openChannel("shell"));
        ((ChannelShell) channelReference.get()).setAgentForwarding(true);
        ((ChannelShell) channelReference.get()).setPtyType("vt102");
        channelOutStreamReference.set(channelReference.get().getOutputStream());
        channelReference.get().connect();
        inputStreamReference.set(channelReference.get().getInputStream());
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

    private long getTimeoutThreshold(SshClientConfiguration configuration) {
        long timeoutThreshold = configuration.getReadTimeout();
        if (timeoutThreshold <= 0) {
            timeoutThreshold = 1000 * 60 * 30; //30 minutes
        }
        return timeoutThreshold;
    }

    private class ShellOutputReader extends Thread {
        private volatile boolean complete;
        private int outputReaderId;
        private volatile boolean wakeUp;
        private AtomicLong sleepTime;

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
                while (!done /* && (totalSleepTime < timeoutThreshold)*/) {
                    try {
                        synchronized (sessionReference) {
                            in = inputStreamReference.get();
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
                            System.out.println(System.currentTimeMillis() + " - Awakened the sleeping thread with ID : " + outputReaderId);
                        }
                        if (done) {
                            return;
                        }
                        //Ignore this exception, as it is expected when user initiates stop request for the thread.
                        //e.printStackTrace();
                    } catch (CallbackException e) {
                        //Callback failed, close and move on
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
                    System.out.println(System.currentTimeMillis() + " - Node : " + nodeCredentials.getHost() + " SSH Shell Tread sent done() to callback : id = " + outputReaderId);
                }
                System.out.println(System.currentTimeMillis() + " - Node : " + nodeCredentials.getHost() + " SSH Shell Tread closing : id = " + outputReaderId);
            }
        }
    }
}
