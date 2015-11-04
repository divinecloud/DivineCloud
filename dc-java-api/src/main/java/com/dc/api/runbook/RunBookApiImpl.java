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


import com.dc.DcException;
import com.dc.api.support.ConnectTask;
import com.dc.api.support.ExecutionIdGenerator;
import com.dc.api.support.FileBasedCredentialsProvider;
import com.dc.api.support.SshClientAccessor;
import com.dc.runbook.RunBookException;
import com.dc.runbook.dt.domain.Property;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.domain.RunBookStep;
import com.dc.runbook.dt.domain.item.ItemType;
import com.dc.runbook.dt.domain.item.RunBookItem;
import com.dc.runbook.dt.domain.item.ScriptItem;
import com.dc.runbook.dt.yaml.RunBookReader;
import com.dc.runbook.rt.CredentialsProvider;
import com.dc.runbook.rt.domain.DtProperty;
import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.runbook.rt.domain.TransformedRunBook;
import com.dc.runbook.rt.exec.*;
import com.dc.runbook.rt.exec.output.OutputFileStore;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.exec.vo.Credential;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.support.CredentialsFileParser;
import com.dc.ssh.client.support.NodeCredentialsFileParser;
import com.dc.util.batch.BatchExecutorService;
import com.dc.util.batch.BatchUnitTask;
import com.dc.util.condition.ConditionalBarrier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RunBookApiImpl implements RunBookApi {

    private int batchSize;
    private SshClientAccessor sshClientAccessor;
    private ExecutionIdGenerator idGenerator;
    private Map<String, RunbookExecutor> runbookExecutorCache;
    private RunbookExecutionCacheCleaner cleaner;

    public RunBookApiImpl(int batchSize) {
        this.batchSize = batchSize;
        this.sshClientAccessor = new SshClientAccessor();
        idGenerator = new ExecutionIdGenerator();
        runbookExecutorCache = new ConcurrentHashMap<>();
        cleaner = new RunbookExecutionCacheCleaner();
    }

    @Override
    public String execute(File nodesPerStepFile, File runbook, RunbookCallback callback, File propertiesFile) {
        String nodesCredText;

        try {
            nodesCredText = new String(Files.readAllBytes(Paths.get(nodesPerStepFile.getAbsolutePath())));
        } catch (IOException e) {
            throw new DcException("Cannot read Nodes Cred File : " + nodesPerStepFile.getAbsolutePath(), e);
        }
        List<List<NodeCredentials>> nodesPerStep = NodeCredentialsFileParser.parse(nodesCredText);

        return execute(nodesPerStep, runbook, callback, propertiesFile);
    }

    @Override
    public String execute(File nodesPerStepFile, File runbook, RunbookCallback callback, File credentialsProviderFile, File propertiesFile) {
        String nodesCredText;
        CredentialsProvider credentialsProvider = null;
        try {
            nodesCredText = new String(Files.readAllBytes(Paths.get(nodesPerStepFile.getAbsolutePath())));
        } catch (IOException e) {
            throw new DcException("Cannot read Nodes Cred File : " + nodesPerStepFile.getAbsolutePath(), e);
        }
        List<List<NodeCredentials>> nodesPerStep = NodeCredentialsFileParser.parse(nodesCredText);

        String credsText;

        try {
            credsText = new String(Files.readAllBytes(Paths.get(credentialsProviderFile.getAbsolutePath())));
            Map<String, Credential> credentialMap = CredentialsFileParser.parse(credsText);
            if(credentialMap != null) {
                credentialsProvider = new FileBasedCredentialsProvider(credentialMap);
            }

        } catch (IOException e) {
            throw new DcException("Cannot read Cred File : " + credentialsProviderFile.getAbsolutePath(), e);
        }
        return execute(nodesPerStep, runbook, callback, credentialsProvider, propertiesFile);
    }

    @Override
    public void execute(File nodesPerStepFile, File runbookFile, File outputFile, File credentialsProviderFile, File propertiesFile, boolean emitOutput) {
        OutputFileStore fileStore = new OutputFileStore(outputFile);
        RunBook runBook = RunBookReader.read(runbookFile);
        List<DtProperty> properties = readProperties(propertiesFile);
        String jsonProperties = null;

        if(propertiesFile.getName().endsWith(".json")) {
            try {
                jsonProperties = new String(Files.readAllBytes(Paths.get(propertiesFile.getAbsolutePath())));
            } catch (IOException e) {
                throw new RunBookException("Cannot read json properties file : " + propertiesFile.getAbsolutePath(), e);
            }
        }
        else {
            properties = readProperties(propertiesFile);
        }

        String executionId = generateExecutionId();
        setDefaultsForRunBook(runBook);
        TransformedRunBook transformedRunBook = DtRunBookConverter.convert(runBook);
        if(jsonProperties != null) {
            transformedRunBook.setPropertiesJson(jsonProperties);
        }

        DtRunbook dtRunbook = convertToDtRunbook(transformedRunBook, properties);
        String nodesCredText;
        CredentialsProvider credentialsProvider = null;
        try {
            nodesCredText = new String(Files.readAllBytes(Paths.get(nodesPerStepFile.getAbsolutePath())));
        } catch (IOException e) {
            throw new DcException("Cannot read Nodes Cred File : " + nodesPerStepFile.getAbsolutePath(), e);
        }
        List<List<NodeCredentials>> nodesPerStep = NodeCredentialsFileParser.parse(nodesCredText);

        String credsText;

        try {
            credsText = new String(Files.readAllBytes(Paths.get(credentialsProviderFile.getAbsolutePath())));
            Map<String, Credential> credentialMap = CredentialsFileParser.parse(credsText);
            if(credentialMap != null) {
                credentialsProvider = new FileBasedCredentialsProvider(credentialMap);
            }

        } catch (IOException e) {
            throw new DcException("Cannot read Cred File : " + credentialsProviderFile.getAbsolutePath(), e);
        }

        Map<String, List<String>> nodesMap = listToMap(nodesPerStep);

        //Map<String, SshClient> sshClientMap = generateSshClientMap(nodesPerStep, executionId);
        ConditionalBarrier<String> barrier = new ConditionalBarrier<>();
        String blockingKey = "RunBookExecCompleteBlock-" + executionId;
        RunBookApiCallback callback = new RunBookApiCallback(dtRunbook, executionId, fileStore, transformedRunBook, barrier, blockingKey, emitOutput);

        execute(executionId, nodesPerStep, runBook, callback, credentialsProvider, properties);

        barrier.block(blockingKey);
    }

    @Override
    public String execute(List<List<NodeCredentials>> nodesPerStep, File runbookFile, RunbookCallback callback, File propertiesFile) {
        RunBook runBook = RunBookReader.read(runbookFile);
        List<DtProperty> properties = readProperties(propertiesFile);
        String jsonProperties = null;

        if(propertiesFile.getName().endsWith(".json")) {
            try {
                jsonProperties = new String(Files.readAllBytes(Paths.get(propertiesFile.getAbsolutePath())));
            } catch (IOException e) {
                throw new RunBookException("Cannot read json properties file : " + propertiesFile.getAbsolutePath(), e);
            }
        }
        else {
            properties = readProperties(propertiesFile);
        }

        String executionId = generateExecutionId();
        setDefaultsForRunBook(runBook);
        TransformedRunBook transformedRunBook = DtRunBookConverter.convert(runBook);
        if(jsonProperties != null) {
            transformedRunBook.setPropertiesJson(jsonProperties);
        }

        DtRunbook dtRunbook = convertToDtRunbook(transformedRunBook, properties);
        Map<String, List<String>> nodesMap = listToMap(nodesPerStep);
        Map<String, SshClient> sshClientMap = generateSshClientMap(nodesPerStep, executionId);
        RunbookExecutor runbookExecutor = new RunbookExecutor(new RunbookContext(executionId, 1, dtRunbook, nodesMap, callback, sshClientMap, cleaner));
        runbookExecutor.execute();
        runbookExecutorCache.put(executionId, runbookExecutor);
        return executionId;

    }

    private List<DtProperty> readProperties(File propertiesFile) {
        List<DtProperty> result = null;
        if(propertiesFile != null && propertiesFile.exists() && propertiesFile.isFile()) {
            result = new ArrayList<>();
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(propertiesFile));
                Set<String> propertyNames = properties.stringPropertyNames();
                if(propertyNames != null) {
                    for(String name : propertyNames) {
                        DtProperty dtProperty = new DtProperty();
                        Property property = new Property();
                        property.setName(name);
                        dtProperty.setValue((String)properties.get(name));
                        dtProperty.setStepProperty(property);
                        result.add(dtProperty);
                    }
                }

            } catch (IOException e) {
                throw new RunBookException("Unable to read properties from file : " + propertiesFile.getAbsolutePath(), e);
            }
        }

        return result;
    }

    @Override
    public String execute(List<List<NodeCredentials>> nodesPerStep, RunBook runbook, RunbookCallback callback, List<DtProperty> properties) {
        String executionId = generateExecutionId();
        return execute(executionId, nodesPerStep, runbook, callback, properties);
    }

    private String execute(String executionId, List<List<NodeCredentials>> nodesPerStep, RunBook runbook, RunbookCallback callback, List<DtProperty> properties) {
        setDefaultsForRunBook(runbook);
        TransformedRunBook transformedRunBook = DtRunBookConverter.convert(runbook);
        DtRunbook dtRunbook = convertToDtRunbook(transformedRunBook, properties);
        Map<String, List<String>> nodesMap = listToMap(nodesPerStep);
        Map<String, SshClient> sshClientMap = generateSshClientMap(nodesPerStep, executionId);
        RunbookExecutor runbookExecutor = new RunbookExecutor(new RunbookContext(executionId, 1, dtRunbook, nodesMap, callback, sshClientMap, cleaner));
        runbookExecutor.execute();
        runbookExecutorCache.put(executionId, runbookExecutor);
        return executionId;
    }

    @Override
    public String execute(List<List<NodeCredentials>> nodesPerStep, File runbookFile, RunbookCallback callback, CredentialsProvider credentialsProvider, File propertiesFile) {
        RunBook runBook = RunBookReader.read(runbookFile);
        List<DtProperty> properties = readProperties(propertiesFile);
        String jsonProperties = null;

        if(propertiesFile.getName().endsWith(".json")) {
            try {
                jsonProperties = new String(Files.readAllBytes(Paths.get(propertiesFile.getAbsolutePath())));
            } catch (IOException e) {
                throw new RunBookException("Cannot read json properties file : " + propertiesFile.getAbsolutePath(), e);
            }
        }
        else {
            properties = readProperties(propertiesFile);
        }

        String executionId = generateExecutionId();
        setDefaultsForRunBook(runBook);
        TransformedRunBook transformedRunBook = DtRunBookConverter.convert(runBook);
        if(jsonProperties != null) {
            transformedRunBook.setPropertiesJson(jsonProperties);
        }

        DtRunbook dtRunbook = convertToDtRunbook(transformedRunBook, properties);
        Map<String, List<String>> nodesMap = listToMap(nodesPerStep);
        Map<String, SshClient> sshClientMap = generateSshClientMap(nodesPerStep, executionId);
        RunbookExecutor runbookExecutor = new RunbookExecutor(new RunbookContext(executionId, 1, dtRunbook, nodesMap, callback, sshClientMap, cleaner), credentialsProvider);
        runbookExecutor.execute();
        runbookExecutorCache.put(executionId, runbookExecutor);
        return executionId;
    }

    @Override
    public String execute(List<List<NodeCredentials>> nodesPerStep, RunBook runbook, RunbookCallback callback, CredentialsProvider credentialsProvider, List<DtProperty> properties) {
        String executionId = generateExecutionId();
        return execute(executionId, nodesPerStep, runbook, callback, credentialsProvider, properties);
    }

    private String execute(String executionId, List<List<NodeCredentials>> nodesPerStep, RunBook runbook, RunbookCallback callback, CredentialsProvider credentialsProvider, List<DtProperty> properties) {
        setDefaultsForRunBook(runbook);
        TransformedRunBook transformedRunBook = DtRunBookConverter.convert(runbook);
        DtRunbook dtRunbook = convertToDtRunbook(transformedRunBook, properties);
        Map<String, List<String>> nodesMap = listToMap(nodesPerStep);
        Map<String, SshClient> sshClientMap = generateSshClientMap(nodesPerStep, executionId);
        RunbookExecutor runbookExecutor = new RunbookExecutor(new RunbookContext(executionId, 1, dtRunbook, nodesMap, callback, sshClientMap, cleaner), credentialsProvider);
        runbookExecutor.execute();
        runbookExecutorCache.put(executionId, runbookExecutor);
        return executionId;

    }

    @Override
    public void cancel(String executionId) {
        RunbookExecutor runbookExecutor = runbookExecutorCache.get(executionId);
        if(runbookExecutor != null) {
            runbookExecutor.cancel();
        }
    }


    private DtRunbook convertToDtRunbook(TransformedRunBook transformedRunBook, List<DtProperty> properties) {

        return new DtRunbook("NotApplicable", "NotApplicable", UUID.randomUUID().toString(), transformedRunBook.getSteps(),  properties, transformedRunBook.isUtilityMode(),
                transformedRunBook.getGeneratedPropertiesFilePath(), transformedRunBook.getPropertiesJson(), transformedRunBook.getRunBookPath(), transformedRunBook.getTransientNodes());
    }

    private Map<String, SshClient> generateSshClientMap(List<List<NodeCredentials>> nodesPerStep, String executionId) throws RunBookException {
        Set<NodeCredentials> nodeCredentialsSet = new HashSet<>();
        for(List<NodeCredentials> nodesList : nodesPerStep) {
            nodeCredentialsSet.addAll(nodesList.stream().collect(Collectors.toList()));
        }
        List<BatchUnitTask> batchUnitTaskList = new ArrayList<>();
        Iterator<NodeCredentials> iterator = nodeCredentialsSet.iterator();
        while(iterator.hasNext()) {
            NodeCredentials cred = iterator.next();
            ConnectTask connectTask = new ConnectTask(executionId, sshClientAccessor, cred);
            batchUnitTaskList.add(connectTask);
        }
        BatchExecutorService batchExecutorService = new BatchExecutorService(batchSize, batchUnitTaskList);
        try {
            batchExecutorService.execute();
        } catch (InterruptedException e) {
            e.printStackTrace(); //@TODO: Add appropriate logic to handle interruption
        }

        for(BatchUnitTask task : batchUnitTaskList) {
            ConnectTask connectTask = (ConnectTask)task;
            if(!connectTask.isSuccess()) {
                throw new RunBookException("Unable to connect to node : " + connectTask.getNodeCred().getHost());
            }
        }

        Map<String, SshClient> sshClientMap = new HashMap<>();
        iterator = nodeCredentialsSet.iterator();
        while(iterator.hasNext()) {
            NodeCredentials cred = iterator.next();
            sshClientMap.put(cred.getId(), sshClientAccessor.get(cred.getId()));
        }
        return sshClientMap;
    }


    private Map<String, List<String>> listToMap(List<List<NodeCredentials>> nodesPerStep) {
        Map<String, List<String>> map = new HashMap<>();
        String idPrefix = "1_";
        for(int i=0; i<nodesPerStep.size(); i++) {
            List<NodeCredentials> nodes = nodesPerStep.get(i);
            map.put(idPrefix + (i + 1), convertToIds(nodes));
        }
        return map;
    }

    private List<String> convertToIds(List<NodeCredentials> nodes) {
        List<String> result = new ArrayList<>();
        for(NodeCredentials nodeCred : nodes) {
            result.add(nodeCred.getId());
        }
        return result;
    }

    private String generateExecutionId() {
        return idGenerator.next();
    }

    private void setDefaultsForRunBook(RunBook runBook) {
        if(runBook != null) {
            List<RunBookStep> steps = runBook.getSteps();

            if(steps != null && steps.size() > 0) {
                for(RunBookStep step : steps) {
                    if(step.getItemType() == ItemType.Script) {
                        RunBookItem runBookItem = step.getItem();
                        ScriptItem item = (ScriptItem)runBookItem;
                        if(item.getLanguage() == null) {
                            item.setLanguage(ScriptLanguage.Shell);
                            if(item.getInvokingProgram() == null) {
                                item.setInvokingProgram("/bin/sh");
                            }
                        }
                    }
                }
            }
        }
    }

    class RunbookExecutionCacheCleaner implements ExecutionDoneNotifier {

        @Override
        public void doneNotification(String executionId) {
            runbookExecutorCache.remove(executionId);
        }
    }
}

