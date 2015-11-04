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


import com.dc.api.runbook.exec.RunBookApiExecStatus;
import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.runbook.rt.domain.NodeOutputChunk;
import com.dc.runbook.rt.domain.TransformedRunBook;
import com.dc.runbook.rt.domain.item.DtRunbookItem;
import com.dc.runbook.rt.exec.ExecState;
import com.dc.runbook.rt.exec.RunbookCallback;
import com.dc.runbook.rt.exec.RunbookStatus;
import com.dc.runbook.rt.exec.output.OutputStore;
import com.dc.runbook.rt.exec.output.StepExecutionStatus;
import com.dc.util.condition.ConditionalBarrier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RunBookApiCallback implements RunbookCallback {

    private DtRunbook runbook;
    private String	          executionId;
    private RunbookStatus execStatus;
    private OutputStore store;
    private volatile boolean	latestStepFailed;
    private ConditionalBarrier<String> barrier;
    private String blockingKey;
    private boolean emitOutput;

    public RunBookApiCallback(DtRunbook runbook, String executionId, OutputStore store, TransformedRunBook transformedRunbook, ConditionalBarrier<String> barrier, String blockingKey, boolean emitOutput) {
        this.runbook = runbook;
        this.executionId = executionId;
        this.store = store;
        this.barrier = barrier;
        this.blockingKey = blockingKey;
        this.emitOutput = emitOutput;
        execStatus = new RunBookApiExecStatus();
        execStatus.setExecutionId(executionId);
        //execStatus.setNodesMap(nodesMap);
        execStatus.setTransformedRunbook(transformedRunbook);
    }

    @Override
    public DtRunbook getRunbook() {
        return runbook;
    }

    @Override
    public String getExecutionId() {
        return executionId;
    }

    @Override
    public StepExecutionStatus executingItem(DtRunbookItem runbookItem) {
        System.out.println(System.currentTimeMillis() + " - ITEM " + runbookItem.getItemId() + " executing");
        StepExecutionStatus itemStatus = new StepExecutionStatus(runbookItem.getItemId());
        itemStatus.setStartTime(System.currentTimeMillis());
        execStatus.addItemStatus(runbookItem.getItemId(), itemStatus);
        store.create(itemStatus);
        return itemStatus;
    }

    @Override
    public boolean didLatestStepFail() {
        return latestStepFailed;
    }

    @Override
    public StepExecutionStatus completedItem(DtRunbookItem runbookItem) {
        StepExecutionStatus itemStatus = (StepExecutionStatus) execStatus.getItemStatus(runbookItem.getItemId());
        itemStatus.setComplete(true);
        itemStatus.setEndTime(System.currentTimeMillis());
        store.update(execStatus);
        return itemStatus;
    }

    @Override
    public StepExecutionStatus skippingItem(DtRunbookItem runbookItem) {
        return null; // @TODO: To add appropriate logic for this
    }

    @Override
    public StepExecutionStatus resumedItem(DtRunbookItem runbookItem) {
        System.out.println(System.currentTimeMillis() + " - ITEM " + runbookItem.getItemId() + " resumed.");
        StepExecutionStatus itemStatus = (StepExecutionStatus) execStatus.getItemStatus(runbookItem.getItemId());
        itemStatus.setComplete(false);
        itemStatus.setState(ExecState.RESUMED);
        store.update(itemStatus);
        return itemStatus;
    }

    @Override
    public StepExecutionStatus pausedItem(DtRunbookItem runbookItem) {
        System.out.println(System.currentTimeMillis() + " - ITEM " + runbookItem.getItemId() + " paused.");
        StepExecutionStatus itemStatus = new StepExecutionStatus(runbookItem.getItemId());
        itemStatus.setStartTime(System.currentTimeMillis());
        itemStatus.setState(ExecState.PAUSED);
        execStatus.addItemStatus(runbookItem.getItemId(), itemStatus);
        store.update(execStatus);
        return itemStatus;
    }

    @Override
    public void output(NodeOutputChunk nodeOutputChunk) {
        appendToOutputMap(nodeOutputChunk);
    }

    private void appendToOutputMap(NodeOutputChunk nodeOutputChunk) {
        Map<String, Map<String, String>> outputMap = execStatus.getOutputMap();
        if (!outputMap.containsKey(nodeOutputChunk.getRunbookItemId())) {
            outputMap.put(nodeOutputChunk.getRunbookItemId(), new ConcurrentHashMap<>());
        }
        if (!outputMap.get(nodeOutputChunk.getRunbookItemId()).containsKey(nodeOutputChunk.getDisplayId())) {
            outputMap.get(nodeOutputChunk.getRunbookItemId()).put(nodeOutputChunk.getDisplayId(), "");
        }
        String currentOutput = outputMap.get(nodeOutputChunk.getRunbookItemId()).get(nodeOutputChunk.getDisplayId());
        outputMap.get(nodeOutputChunk.getRunbookItemId()).put(nodeOutputChunk.getDisplayId(), currentOutput + nodeOutputChunk.getOutputChunk());
    }

    @Override
    public void started() {
        System.out.println(System.currentTimeMillis() + " - RUNBOOK started");
        execStatus.setStartTime(System.currentTimeMillis());
        store.create(execStatus);
    }

    @Override
    public void markCancelled() {
        System.out.println(System.currentTimeMillis() + " - RUNBOOK marked cancelled");
    }

    @Override
    public void done() {
            System.out.println(System.currentTimeMillis() + " - RUNBOOK complete");
        try {
            execStatus.setEndTime(System.currentTimeMillis());
            execStatus.setComplete(true);
            execStatus.setState(didLatestStepFail() ? ExecState.FAILED : ExecState.SUCCESSFUL);
            store.update(execStatus);
            store.done();
        }
        finally {
            barrier.release(blockingKey);
        }
    }

    @Override
    public void done(Exception e) {
        try {
            execStatus.setEndTime(System.currentTimeMillis());
            // TODO: handle exception later.
            e.printStackTrace();
            execStatus.setComplete(true);
            execStatus.setState(ExecState.FAILED);
            store.update(execStatus);
            store.done();
        }
        finally {
            barrier.release(blockingKey);
        }
    }

//    private String getCommaSeparatedNodesString(List<NodeSelectionMap> selections) {
//        Collection<String> nodesList = convertToMap(execStatus.getNodesMap()).values();
//
//        StringBuilder sb = new StringBuilder();
//        int i = 0;
//        for (String id : nodesList) {
//            if (i > 0) {
//                sb.append(",");
//            }
//            sb.append(id);
//            i++;
//        }
//        return sb.toString();
//    }

//    private Map<String, String> convertToMap(List<NodeSelectionMap> selections) {
//        Map<String, String> map = new HashMap<>();
//        for (NodeSelectionMap selectionMap : selections) {
//            List<String> idList = selectionMap.getDisplayIdList();
//            if(idList != null) {
//                for (String id : idList) {
//                    map.put(id, id);
//                }
//            }
//        }
//        return map;
//    }

    @Override
    public void error(NodeOutputChunk nodeOutputChunk) {
        output(nodeOutputChunk);
    }

    @Override
    public void itemExecOnNodeDone(String itemId, String nodeId, int statusCode, String message) {
        synchronized (this) {
            if (statusCode != 0) {
                latestStepFailed = true;
            } else {
                if (!latestStepFailed) {
                    latestStepFailed = false;
                }
            }
        }
        StepExecutionStatus itemStatus = (StepExecutionStatus) execStatus.getItemStatus(itemId);
        itemStatus.addNodeStatus(nodeId, statusCode, message);
        if(message != null ) {
            NodeOutputChunk outputChunk = new NodeOutputChunk(nodeId, message, itemId);
            output(outputChunk);
        }
    }
}
