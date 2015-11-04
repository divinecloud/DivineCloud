package com.dc.runbook.rt.exec.support;

import com.dc.runbook.rt.domain.NodeOutputChunk;
import com.dc.runbook.rt.exec.RunbookCallback;
import com.dc.ssh.client.CommandExecutionCallback;
import com.dc.ssh.client.SshException;
import com.dc.util.condition.ConditionalBarrier;

public class RunbookSshCallback implements CommandExecutionCallback {

	private RunbookCallback	           callback;
	private String	                   displayId;
	private String	                       runbookItemId;
	private int	                       statusCode	= 0;
	private SshException	           cause;
	private ConditionalBarrier<String>	conditionalBarrier;
    private boolean cancelled;

	public RunbookSshCallback(RunbookCallback callback, String displayId, String runbookItemId, ConditionalBarrier<String> conditionalBarrier) {
		this.callback = callback;
		this.displayId = displayId;
		this.runbookItemId = runbookItemId;
		this.conditionalBarrier = conditionalBarrier;
	}

	@Override
	public void outputData(byte[] output) {
        String data = ColorCodeFilter.filter(new String(output));
		NodeOutputChunk chunk = new NodeOutputChunk(displayId, data, runbookItemId);
        callback.output(chunk);
	}

	@Override
	public void errorData(byte[] error) {
        String data = ColorCodeFilter.filter(new String(error));
		NodeOutputChunk chunk = new NodeOutputChunk(displayId, data, runbookItemId);
		callback.error(chunk);
	}

	@Override
	public void done(int statusCode) {
        this.statusCode = statusCode;
		callback.itemExecOnNodeDone(runbookItemId, displayId, statusCode, null);
		conditionalBarrier.release(displayId + "_" + runbookItemId);
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

    @Override
    public void executionCancelled() {
        cancelled = true;
    }

    @Override
	public void done(SshException cause) {
		statusCode = 999;
		cause.printStackTrace();
		this.cause = cause;
		callback.itemExecOnNodeDone(runbookItemId, displayId, statusCode, cause.getMessage());
		conditionalBarrier.release(displayId + "_" + runbookItemId);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public SshException getCause() {
		return cause;
	}

}
