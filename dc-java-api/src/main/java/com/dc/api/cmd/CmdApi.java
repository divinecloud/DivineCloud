package com.dc.api.cmd;

import com.dc.api.exec.NodeExecutionDetails;
import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.ssh.client.exec.cmd.SshCommand;
import com.dc.ssh.client.exec.vo.NodeCredentials;

import java.util.List;

public interface CmdApi {

    public List<NodeExecutionDetails> execute(List<NodeCredentials> nodeCredentials, String command);

    public String execute(List<NodeCredentials> nodeCredentials, String command, GroupTermCallback callback);

    public List<NodeExecutionDetails> execute(List<NodeCredentials> nodeCredentials, SshCommand command);

    public String execute(List<NodeCredentials> nodeCredentials, SshCommand command, GroupTermCallback callback);

    public void cancel(List<NodeCredentials> nodeCredentials, String executionId);

}
