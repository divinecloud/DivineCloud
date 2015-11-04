package com.dc.runbook.rt.cmd.exec;


public interface ExecutionCompleteEvent {
    public void complete(String executionId, GroupTermExecStatus groupTermExecStatus);
}
