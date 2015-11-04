/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
