package com.dc.runbook.rt.cmd.exec;

public interface GroupTermOutputTransmitter {
    public void transmit(GroupTermExecStatusChunk outputChunk);
}
