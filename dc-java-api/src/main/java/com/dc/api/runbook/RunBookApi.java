package com.dc.api.runbook;

import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.rt.CredentialsProvider;
import com.dc.runbook.rt.domain.DtProperty;
import com.dc.runbook.rt.exec.RunbookCallback;
import com.dc.ssh.client.exec.vo.NodeCredentials;

import java.io.File;
import java.util.List;

public interface RunBookApi {


    public String execute(File nodesPerStepFile, File runbook, RunbookCallback callback, File propertiesFile);

    public String execute(File nodesPerStepFile, File runbook, RunbookCallback callback, File credentialsProviderFile, File propertiesFile);

    public void execute(File nodesPerStepFile, File runbook, File outputFile, File credentialsProviderFile, File propertiesFile, boolean emitOutput);


    public String execute(List<List<NodeCredentials>> nodesPerStep, File runbook, RunbookCallback callback, File propertiesFile);

    public String execute(List<List<NodeCredentials>> nodesPerStep, File runbook, RunbookCallback callback, CredentialsProvider credentialsProvider, File propertiesFile);


    public String execute(List<List<NodeCredentials>> nodesPerStep, RunBook runbook, RunbookCallback callback, List<DtProperty> properties);

    public String execute(List<List<NodeCredentials>> nodesPerStep, RunBook runbook, RunbookCallback callback, CredentialsProvider credentialsProvider, List<DtProperty> properties);


    public void cancel(String executionId);

}
