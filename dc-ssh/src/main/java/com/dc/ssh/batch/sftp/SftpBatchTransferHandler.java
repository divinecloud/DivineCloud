package com.dc.ssh.batch.sftp;

import com.dc.ssh.client.exec.SshClient;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.ssh.client.sftp.stream.SftpStreamer;
import com.dc.support.KeyValuePair;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SftpBatchTransferHandler extends Thread implements TaskCompleteNotification, Cancellable {
    private SftpBatchContext context;
    private Map<String, KeyValuePair<Integer, AtomicInteger>> executionCompletionCountMap;
    private SftpBatchExecutorService service;
    private int threadsCount;
    private List<KeyValuePair<SshClient, SftpStreamer>> list;
    private LinkedBlockingQueue queue;

    public SftpBatchTransferHandler(SftpBatchContext context, int threadsCount, List<KeyValuePair<SshClient, SftpStreamer>> list) {
        this.context = context;
        this.threadsCount = threadsCount;
        this.list = list;
        initialize();
    }

    private void initialize() {
        executionCompletionCountMap = new ConcurrentHashMap<>();
        executionCompletionCountMap.put(context.getExecutionId(), new KeyValuePair<>(context.getSshClients().size(), new AtomicInteger()));
        service = new SftpBatchExecutorService(context.getExecutionId(), threadsCount, this, true);
        queue = new LinkedBlockingQueue(list.size());
    }

    public void run() {
        for(int i=0; i<list.size(); i++) {
            if(context.getMode() == SftpMode.DOWNLOAD) {
                service.submit(new DownloadTask(context));
            }
            else {
                service.submit(new UploadTask(context));
            }
        }
        transfer();
    }

    private void transfer() {
        if(context.getMode() == SftpMode.UPLOAD) {
            uploadFile();
        }
        else {
            downloadFile();
        }
    }

    private void uploadFile() {
        try {
            for(KeyValuePair<SshClient, SftpStreamer> pair : list) {
                queue.put(new TaskMessage(pair, context.getFrom()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile() {
        try {
            for(KeyValuePair<SshClient, SftpStreamer> pair : list) {
                queue.put(new TaskMessage(pair, context.getTo()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        service.close();
        context.getBatchCallback().cancelled();
    }

    @Override
    public void taskComplete(String id) {
        KeyValuePair<Integer, AtomicInteger> pair = executionCompletionCountMap.get(id);
        if(pair != null) {
            int count = pair.getValue().incrementAndGet();
            if(count == pair.getKey()) {
                context.getBatchCallback().done();
                service.close();
            }
        }

    }

    private int calculatePercentage(long totalSize, long currentSize) {
        int result = (int)((currentSize * 100) / totalSize);
        return result;
    }

    class UploadTask implements Callable {
        private SftpBatchContext context;

        public UploadTask(SftpBatchContext context) {
            this.context = context;
        }

        @Override
        public Object call() throws Exception {
            TaskMessage message = (TaskMessage)queue.take();
            SftpStreamer sftpStreamer = message.getSftpStreamer();
            if(context.getFrom() == null || context.getFrom().trim().length() == 0) {
                context.getBatchCallback().completeForNode(message.getSshClient().id(), new SftpClientException("Invalid source path provided : " + context.getFrom()));
                return null;
            }
            File sourceFile = new File(context.getFrom());
            if(!sourceFile.exists() || sourceFile.isDirectory()) {
                context.getBatchCallback().completeForNode(message.getSshClient().id(), new SftpClientException("Invalid source path provided : " + context.getFrom()));
                return null;
            }
            long length = sourceFile.length();
            System.out.println("length = " + length);
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
                int byteSize = 130000;
                byte[] buffer = new byte[byteSize];
                int count = 0;
                byte[] bytes;
                long bytesTransferred = 0;
                int previousPercentage = 0;
                int latestPercentage = 0;
                while((count = bis.read(buffer)) != -1) {
                    bytes = buffer;
                    if(count < byteSize) {
                        bytes = new byte[count];
                        System.arraycopy(buffer, 0, bytes, 0, count);
                    }
                    bytesTransferred += bytes.length;
                    sftpStreamer.write(bytes);
                    latestPercentage = calculatePercentage(length, bytesTransferred);
                    if(latestPercentage > previousPercentage) {
                        context.getBatchCallback().percentageCompleteForNode(sftpStreamer.id(), latestPercentage);
                        previousPercentage = latestPercentage;
                    }
                }
                bis.close();
                context.getBatchCallback().completeForNode(sftpStreamer.id());
            }
            catch(Throwable t) {
                t.printStackTrace();
                context.getBatchCallback().completeForNode(message.getSshClient().id(), new SftpClientException("Unable to write to the target location :" + sourceFile.getAbsolutePath(), t));
            }
            finally {
                if(sftpStreamer != null) {
                    sftpStreamer.close();
                }
            }


            //@TODO: Handle closing of SftpStreamer instances.
            return null;
        }
    }

    class DownloadTask implements Callable {
        private SftpBatchContext context;

        public DownloadTask(SftpBatchContext context) {
            this.context = context;
        }

        @Override
        public Object call() throws Exception {
            TaskMessage message = (TaskMessage)queue.take();
            if(message.getPath() == null || message.getPath().lastIndexOf("/") == -1 || message.getPath().length() == 0) {
                context.getBatchCallback().completeForNode(message.getSshClient().id(), new SftpClientException("Invalid destination path provided : " + message.getPath()));
                return null;
            }
            String folderPath = message.getPath().substring(0, message.getPath().lastIndexOf("/")) + "/" + message.getSshClient().id();
            File file = new File(folderPath);
            file.mkdirs();
            String fileName = message.getPath().substring(message.getPath().lastIndexOf("/") + 1);
            File targetFile = new File(folderPath + "/" + fileName);

            SshClient client = message.getSshClient();
            try {
                ExecutionDetails execDetails = client.execute("wc -c " + context.getFrom());
                if(execDetails.isFailed() || execDetails.getOutput() == null || execDetails.getOutput().length == 0) {
                    context.getBatchCallback().completeForNode(client.id(), new SftpClientException("Unable to get the total file size. Failed with error code : " + execDetails.getStatusCode() + " - " + new String(execDetails.getError())));
                    context.getBatchCallback().completeForNode(client.id());
                    return null;
                }
                long fileLength = parseFileLength(new String(execDetails.getOutput()));
                SftpStreamer sftpStreamer = message.getSftpStreamer();
                byte[] buffer = new byte[130000];
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));
                int count;
                long bytesTransferred = 0;
                int previousPercentage = 0;
                int latestPercentage = 0;
                while((count = sftpStreamer.read(buffer)) != -1) {
                    bos.write(buffer, 0, count);
                    bytesTransferred += count;
                    latestPercentage = calculatePercentage(fileLength, bytesTransferred);
                    if(latestPercentage > previousPercentage) {
                        context.getBatchCallback().percentageCompleteForNode(sftpStreamer.id(), latestPercentage);
                        previousPercentage = latestPercentage;
                    }
                }
                bos.close();
                context.getBatchCallback().completeForNode(sftpStreamer.id());
            }
            catch(Throwable t) {
                t.printStackTrace();
                context.getBatchCallback().completeForNode(message.getSshClient().id(), new SftpClientException("Unable to transfer from file " + context.getFrom() + " to file :" + targetFile.getAbsolutePath(), t));
            }

            //@TODO: Handle closing of SftpStreamer instances.
            return null;
        }

        private long parseFileLength(String wordCountStr) {
            long size = -1;

            String lengthStr = wordCountStr.split(" ")[0];
            if(lengthStr != null && lengthStr.length() > 0) {
                lengthStr = lengthStr.trim();
                size = Long.parseLong(lengthStr);
            }
            return size;
        }
    }
}

class TaskMessage {
    private KeyValuePair<SshClient, SftpStreamer> pair;
    private String path;

    public TaskMessage(KeyValuePair<SshClient, SftpStreamer> pair, String path) {
        this.pair = pair;
        this.path = path;
    }

    public SshClient getSshClient() {
        return pair.getKey();
    }

    public SftpStreamer getSftpStreamer() {
        return pair.getValue();
    }
    public String getPath() {
        return path;
    }
}
