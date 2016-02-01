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

package com.dc.api.cmd;

import com.dc.api.exec.NodeExecutionDetails;
import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.ssh.client.exec.cmd.SshCommand;
import com.dc.ssh.client.exec.vo.NodeCredentials;

import java.util.List;

public interface CmdApi {

    public List<NodeExecutionDetails> execute(List<NodeCredentials> nodeCredentials, String command);

    public String execute(List<NodeCredentials> nodeCredentials, String command, GroupTermCallback callback);

    public List<NodeExecutionDetails> execute(List<NodeCredentials> nodeCredentials, SshCommand command);

    public String execute(List<NodeCredentials> nodeCredentials, SshCommand command, GroupTermCallback callback);

    public void cancel(List<NodeCredentials> nodeCredentials, String executionId);

    public void close();
}
