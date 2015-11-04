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
