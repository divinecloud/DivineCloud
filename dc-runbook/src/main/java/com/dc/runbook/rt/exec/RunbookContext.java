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

package com.dc.runbook.rt.exec;

import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.ssh.client.exec.SshClient;

import java.util.List;
import java.util.Map;

public class RunbookContext {
	private String executionId;
	private int startingStep;
    private DtRunbook runbook;
    private Map<String, List<String>> itemNodesMap;
    private RunbookCallback	           callback;
    private Map<String, SshClient>	   sshClients;
    private ExecutionDoneNotifier notifier;
    public RunbookContext(String executionId, int startingStep, DtRunbook runbook, Map<String, List<String>> itemNodesMap, RunbookCallback callback, Map<String, SshClient> sshClients, ExecutionDoneNotifier notifier) {
    	this.executionId = executionId;
    	this.startingStep = startingStep;
    	this.runbook = runbook;
        this.itemNodesMap = itemNodesMap;
        this.callback = callback;
        this.sshClients = sshClients;
        this.notifier = notifier;
    }

    public DtRunbook getRunbook() {
        return runbook;
    }

    public Map<String, List<String>> getItemNodesMap() {
        return itemNodesMap;
    }

    public RunbookCallback getCallback() {
        return callback;
    }

    public Map<String, SshClient> getSshClients() {
        return sshClients;
    }

	public String getExecutionId() {
		return executionId;
	}

	public int getStartingStep() {
		return startingStep;
	}

    public ExecutionDoneNotifier getNotifier() {
        return notifier;
    }
}
