package com.dc.ssh.client.exec.cmd;


public class FileTransferCommand extends AbstractSshCommand {
    private String destination;
    private byte[] sourceBytes;

    public FileTransferCommand(SshCommandAttributes attributes, String destination, byte[] sourceBytes) {
        super(attributes);
        this.destination = destination;
        this.sourceBytes = sourceBytes;
    }

    public FileTransferCommand(String executionId, String destination, byte[] sourceBytes) {
        super(new SshCommandAttributes(executionId, null, null, false));
        this.destination = destination;
        this.sourceBytes = sourceBytes;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public byte[] getSourceBytes() {
        return sourceBytes;
    }

    public void setSourceBytes(byte[] sourceBytes) {
        this.sourceBytes = sourceBytes;
    }

    @Override
    public String prettyCode() {
        return null;
    }
}
