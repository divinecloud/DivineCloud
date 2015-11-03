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
