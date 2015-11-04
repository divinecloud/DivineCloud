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

package com.dc.runbook.rt.exec;


import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.runbook.rt.domain.NodeOutputChunk;
import com.dc.runbook.rt.domain.item.DtRunbookItem;
import com.dc.runbook.rt.node.OnDemandNodesCleaner;

public class RunbookCallbackAdapter implements RunbookCallback {
    private RunbookCallback callback;
    private OnDemandNodesCleaner cleaner;
    private ExecutionDoneNotifier notifier;
    private String executionId;

    public RunbookCallbackAdapter(RunbookCallback callback, String executionId) {
        this.callback = callback;
        this.executionId = executionId;
    }

    public void registerOnDemandNodesCleaner(OnDemandNodesCleaner cleaner) {
        this.cleaner = cleaner;
    }

    public void registerDoneNotifier(ExecutionDoneNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public DtRunbook getRunbook() {
        return callback.getRunbook();
    }

    @Override
    public RunbookItemStatus executingItem(DtRunbookItem runbookItem) {
        return callback.executingItem(runbookItem);
    }

    @Override
    public RunbookItemStatus completedItem(DtRunbookItem runbookItem) {
        return callback.completedItem(runbookItem);
    }

    @Override
    public RunbookItemStatus skippingItem(DtRunbookItem runbookItem) {
        return callback.skippingItem(runbookItem);
    }

    @Override
    public RunbookItemStatus pausedItem(DtRunbookItem runbookItem) {
        return callback.pausedItem(runbookItem);
    }

    @Override
    public RunbookItemStatus resumedItem(DtRunbookItem runbookItem) {
        return callback.resumedItem(runbookItem);
    }

    @Override
    public void output(NodeOutputChunk nodeOutputChunk) {
        callback.output(nodeOutputChunk);
    }

    @Override
    public void error(NodeOutputChunk nodeOutputChunk) {
        callback.error(nodeOutputChunk);
    }

    @Override
    public void started() {
        callback.started();
    }

    @Override
    public void markCancelled() {
        notifier.doneNotification(executionId);
        cleaner.cleanup();
        callback.markCancelled();
    }

    @Override
    public void done() {
        notifier.doneNotification(executionId);
        cleaner.cleanup();
        callback.done();
    }

    @Override
    public void done(Exception e) {
        notifier.doneNotification(executionId);
        cleaner.cleanup();
        callback.done(e);
    }

    @Override
    public String getExecutionId() {
        return executionId;
    }

    @Override
    public void itemExecOnNodeDone(String itemId, String nodeId, int statusCode, String message) {
        callback.itemExecOnNodeDone(itemId, nodeId, statusCode, message);
    }

    @Override
    public boolean didLatestStepFail() {
        return callback.didLatestStepFail();
    }

}
