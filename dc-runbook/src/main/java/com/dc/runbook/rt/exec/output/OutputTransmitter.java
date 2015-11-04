package com.dc.runbook.rt.exec.output;

import com.dc.runbook.rt.domain.NodeOutputChunk;
import com.dc.runbook.rt.exec.RunbookStatus;

public interface OutputTransmitter {
	public void transmit(RunbookStatus execStatus, NodeOutputChunk outputChunk);
}
