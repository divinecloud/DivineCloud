package com.dc.runbook.rt.exec.output;

import com.dc.runbook.rt.exec.RunbookItemStatus;
import com.dc.runbook.rt.exec.RunbookStatus;

public interface OutputStore {
	public void create(RunbookStatus execStatus);

	public void create(RunbookItemStatus itemStatus);

	public void update(RunbookStatus execStatus);

	public void update(RunbookItemStatus itemStatus);

    public void done();
}
