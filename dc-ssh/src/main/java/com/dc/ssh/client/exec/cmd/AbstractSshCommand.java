package com.dc.ssh.client.exec.cmd;


import com.dc.DcException;

import java.util.List;

public abstract class AbstractSshCommand implements SshCommand {

    private SshCommandAttributes attributes;
    protected long timeoutThreshold;

    public AbstractSshCommand(SshCommandAttributes attributes) {
        if(attributes == null) {
            throw new DcException("Ssh command attributes cannot be null");
        }
        this.attributes = attributes;
    }

    @Override
    public String runAsUser() {
        String result = null;
        RunAsAttributes runAs = attributes.getRunAsAttributes();
        if(runAs != null) {
            result = runAs.getRunAs();
        }
        return result;
    }

    @Override
    public String runAsPassword() {
        String result = null;
        RunAsAttributes runAs = attributes.getRunAsAttributes();
        if(runAs != null) {
            result = runAs.getPassword();
        }
        return result;
    }

    @Override
    public boolean runAsAdmin() {
        boolean result = false;
        RunAsAttributes runAs = attributes.getRunAsAttributes();
        if(runAs != null) {
            result = runAs.isAdmin();
        }
        return result;
    }

    @Override
    public String getExecutionId() {
        return attributes.getExecutionId();
    }

    @Override
    public List<String> answers() {
        return attributes.getAnswers();
    }

    @Override
    public boolean causeReboot() {
        return attributes.isReboot();
    }

    public long getTimeoutThreshold() {
        return timeoutThreshold;
    }

    public void setTimeoutThreshold(long timeoutThreshold) {
        this.timeoutThreshold = timeoutThreshold;
    }
}
