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

