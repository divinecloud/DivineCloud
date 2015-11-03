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
