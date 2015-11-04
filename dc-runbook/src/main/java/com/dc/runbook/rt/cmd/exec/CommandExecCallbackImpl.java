package com.dc.runbook.rt.cmd.exec;

import com.dc.ssh.client.SshException;
import com.dc.ssh.client.CommandExecutionCallback;

public class CommandExecCallbackImpl implements CommandExecutionCallback {
	private ExecutionCallback	callback;
	private int	              statusCode;
    private boolean cancelled;

	public CommandExecCallbackImpl(ExecutionCallback callback) {
		this.callback = callback;
	}

	@Override
	public void outputData(byte[] output) {
		callback.outputData(output);
	}

	@Override
	public void errorData(byte[] error) {
		callback.errorData(error);
	}

	@Override
	public void done(int statusCode) {
		this.statusCode = statusCode;
		callback.done(new CommandExecutionResult.Builder().code(statusCode).build());
	}

    @Override
    public void executionCancelled() {
        cancelled = true;
    }

    @Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public void done(SshException cause) {
		cause.printStackTrace();
		callback.done(new CommandExecutionResult.Builder().failed(true).build());
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public SshException getCause() {
		return null;
	}

}
