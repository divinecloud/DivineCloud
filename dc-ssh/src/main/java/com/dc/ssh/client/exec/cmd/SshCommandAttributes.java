package com.dc.ssh.client.exec.cmd;

import java.util.List;

public class SshCommandAttributes {
    private String executionId;
    private RunAsAttributes runAsAttributes;
    private List<String> answers;
    private boolean reboot;

    public SshCommandAttributes(String executionId) {
        this.executionId = executionId;
    }

    public SshCommandAttributes(String executionId, RunAsAttributes runAsAttributes, List<String> answers, boolean reboot) {
        this.executionId = executionId;
        this.runAsAttributes = runAsAttributes;
        this.answers = answers;
        this.reboot = reboot;
    }

    public String getExecutionId() {
        return executionId;
    }

    public RunAsAttributes getRunAsAttributes() {
        return runAsAttributes;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public boolean isReboot() {
        return reboot;
    }

    @Override
    public String toString() {
        return "SshCommandAttributes{" +
                "executionId='" + executionId + '\'' +
                ", runAsAttributes=" + runAsAttributes +
                ", answers=" + answers +
                ", reboot=" + reboot +
                '}';
    }
}
