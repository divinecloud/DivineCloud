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

import com.dc.ssh.client.support.CommandExecIdGenerator;
import com.dc.ssh.client.support.FileSupport;
import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SftpClientImpl implements SftpClient {
    private Session session;
    private Map<String, BaseWorker> execIdMap;
    private final CommandExecIdGenerator commandExecIdGenerator;

    public SftpClientImpl(Session session) {
        this.session = session;
        execIdMap = new ConcurrentHashMap<>();
        commandExecIdGenerator = new CommandExecIdGenerator("/Sftp/"+System.currentTimeMillis()+ (int)(Math.random() * 9999));
    }

    @Override
    public String putFile(File source, String target, SftpCallback callback) throws SftpClientException {
        String execId = commandExecIdGenerator.generate();
        FilePutWorker worker = new FilePutWorker(execId, source, target, callback);
        execIdMap.put(execId, worker);
        callback.execId(execId);
        worker.start();
        return execId;
    }

    @Override
    public String putFile(byte[] data, String target, SftpCallback callback) throws SftpClientException {
        String execId = commandExecIdGenerator.generate();
        FilePutBytesWorker worker = new FilePutBytesWorker(execId, data, target, callback);
        execIdMap.put(execId, worker);
        callback.execId(execId);
        worker.start();
        return execId;

    }

    @Override
    public String getFile(String source, File target, SftpCallback callback) throws SftpClientException {
        String execId = commandExecIdGenerator.generate();
        FileGetWorker worker = new FileGetWorker(execId, source, target, callback);
        execIdMap.put(execId, worker);
        callback.execId(execId);
        worker.start();
        return execId;
    }

    @Override
    public byte[] getFile(String source) throws SftpClientException {
        ChannelSftp sftpChannel = createSftpChannel();
        return readFile(source, sftpChannel);
    }

    @Override
    public void cancel(String execId) {
        BaseWorker baseWorker = execIdMap.get(execId);
        baseWorker.cancel();
        baseWorker.interrupt();
    }

    private byte[] readFile(String source, ChannelSftp sftpChannel) throws SftpClientException {
        List<Byte> bytesList = new ArrayList<>();
        byte[] result = null;
        BufferedInputStream bis = null;
        try {
            String parent = source.substring(0, source.lastIndexOf('/'));
            String fileName = source.substring(source.lastIndexOf('/') + 1);
            sftpChannel.cd(parent);
            bis = new BufferedInputStream(sftpChannel.get(fileName));
            byte[] buffer = new byte[1024];
            int readCount;
            while( (readCount = bis.read(buffer)) > 0) {
                copy(bytesList, buffer, readCount);
            }
            result = convert(bytesList);
        } catch (SftpException e) {
            throw new SftpClientException("SftpException occurred while file transfer.", e);
        } catch (IOException e) {
            throw new SftpClientException("IOException occurred while writing file using sftp.", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new SftpClientException("IOException occurred while closing OutputStream.", e);
                }
                finally {
                    sftpChannel.disconnect();
                }
            }
        }
        return result;

    }

    private ChannelSftp createSftpChannel() throws SftpClientException {
        Channel channel;
        ChannelSftp channelSftp;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            throw new SftpClientException("Ssh exception occurred while creating session for file transfer.", e);
        }
        return channelSftp;
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

    private class FilePutWorker extends BaseWorker {
        private File source;
        private String target;
        private SftpCallback callback;

        public FilePutWorker(String execId, File source, String target, SftpCallback callback) {
            super(execId);
            this.execId = execId;
            this.source = source;
            this.target = target;
            this.callback = callback;
        }

        public void run() {
            try {
                byte[] sourceBytes = FileSupport.readFile(source);
                writeFile(sourceBytes, target);
                callback.done();
            }
            catch(Throwable t) {
                callback.done(new SftpClientException(t));
            }
            execIdMap.remove(execId);
        }
    }

    private class FilePutBytesWorker extends BaseWorker {
        private byte[] data;
        private String target;
        private SftpCallback callback;

        public FilePutBytesWorker(String execId, byte[] data, String target, SftpCallback callback) {
            super(execId);
            this.data = data;
            this.target = target;
            this.callback = callback;
        }

        public void run() {
            try {
                writeFile(data, target);
            }
            catch(Throwable t) {
                callback.done(new SftpClientException(t));
            }
            execIdMap.remove(execId);
        }
    }

    private class FileGetWorker extends BaseWorker {
        private String source;
        private File target;
        private SftpCallback callback;


        public FileGetWorker(String execId, String source, File target, SftpCallback callback) {
            super(execId);
            this.source = source;
            this.target = target;
            this.callback = callback;
        }

        public void run() {
            sftpChannel = createSftpChannel();
            BufferedOutputStream bos = null;
            try {
                byte[] result = readFile(source, sftpChannel);
                if(!target.exists()) {
                    boolean fileCreated = target.createNewFile();
                    if(!fileCreated) {
                        throw new SftpClientException("Unable to create file : " + target.getAbsolutePath());
                    }
                }
                bos = new BufferedOutputStream(new FileOutputStream(target));
                bos.write(result);
                bos.flush();
                callback.done();

            } catch (FileNotFoundException e) {
                callback.done(new SftpClientException("Unable to write to file : " + target.getAbsolutePath() + " or read file : " + source, e));
            } catch (IOException e) {
                callback.done(new SftpClientException("IOException while trying to write to file : " + target.getAbsolutePath() + " or read file : " + source, e));
            } catch(Throwable t) {
                callback.done(new SftpClientException("Error occurred while trying to write to file : " + target.getAbsolutePath() + " or read file : " + source, t));
            }
            finally {
                if(bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        throw new SftpClientException(e);
                    }
                }
            }
            execIdMap.remove(execId);
        }
    }

    private abstract class BaseWorker extends Thread {
        protected ChannelSftp sftpChannel;
        protected String execId;

        public BaseWorker(String execId) {
            this.execId = execId;
        }

        public void cancel() {
            if(sftpChannel != null && !sftpChannel.isClosed()) {
                sftpChannel.disconnect();
            }
            execIdMap.remove(execId);
        }

        protected void writeFile(byte[] data, String target) throws SftpException, IOException {
            OutputStream out = null;
            sftpChannel = createSftpChannel();
            try {
                String parent = target.substring(0, target.lastIndexOf('/'));
                sftpChannel.cd(parent);
                out = sftpChannel.put(target.substring(target.lastIndexOf('/') + 1));
                out.write(data, 0, data.length);
                out.flush();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        throw new SftpClientException("IOException occurred while closing OutputStream.", e);
                    }
                    finally {
                        sftpChannel.disconnect();
                    }
                }
            }
        }
    }
}
