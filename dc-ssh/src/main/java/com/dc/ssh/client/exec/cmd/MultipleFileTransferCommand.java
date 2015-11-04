package com.dc.ssh.client.exec.cmd;


import com.dc.support.KeyValuePair;

import java.util.List;

public class MultipleFileTransferCommand extends AbstractSshCommand {
    private List<KeyValuePair<String, byte[]>> filesList;

    public MultipleFileTransferCommand(SshCommandAttributes attributes, List<KeyValuePair<String, byte[]>> filesList) {
        super(attributes);
        this.filesList = filesList;
    }

    public MultipleFileTransferCommand(String executionId, List<KeyValuePair<String, byte[]>> filesList) {
        super(new SshCommandAttributes(executionId, null, null, false));
        this.filesList = filesList;
    }

    public List<KeyValuePair<String, byte[]>> getFilesList() {
        return filesList;
    }

    public void setFilesList(List<KeyValuePair<String, byte[]>> filesList) {
        this.filesList = filesList;
    }

    @Override
    public String prettyCode() {
        return null;
    }
}

