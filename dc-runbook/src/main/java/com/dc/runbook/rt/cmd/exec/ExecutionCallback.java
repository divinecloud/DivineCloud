package com.dc.runbook.rt.cmd.exec;

public interface ExecutionCallback {

	public void outputData(byte[] output);

	public void errorData(byte[] output);

	public void done(CommandExecutionResult result);

	public boolean isDone();
}
