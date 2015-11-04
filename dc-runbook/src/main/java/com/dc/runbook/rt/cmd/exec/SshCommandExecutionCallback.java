package com.dc.runbook.rt.cmd.exec;

public class SshCommandExecutionCallback implements ExecutionCallback {

	private String	                      displayId;
    private GroupTermCallback                 groupTermCallback;
    private volatile boolean done;

	public SshCommandExecutionCallback(String displayId, GroupTermCallback groupTermCallback) {
		this.displayId = displayId;
        this.groupTermCallback = groupTermCallback;
	}

	@Override
	public void outputData(byte[] output) {
		if (output != null) {
            groupTermCallback.output(displayId, new String(output));
		}
	}

	@Override
	public void errorData(byte[] output) {
		if (output != null) {
            groupTermCallback.error(displayId, new String(output));
		}
	}

	@Override
	public void done(CommandExecutionResult result) {
        done = true;
        groupTermCallback.complete(displayId, result.getCode());
	}

    @Override
    public boolean isDone() {
        return done;
    }
}
