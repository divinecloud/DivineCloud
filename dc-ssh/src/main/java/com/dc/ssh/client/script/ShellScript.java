package com.dc.ssh.client.script;

import com.dc.exec.ExecutionDetails;

public interface ShellScript {

    public ExecutionDetails execute();

    public void printScript();
}
