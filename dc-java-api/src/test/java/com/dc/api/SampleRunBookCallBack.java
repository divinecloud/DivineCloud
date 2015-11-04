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

package com.dc.api;


import com.dc.runbook.rt.domain.DtRunbook;
import com.dc.runbook.rt.domain.NodeOutputChunk;
import com.dc.runbook.rt.domain.item.DtRunbookItem;
import com.dc.runbook.rt.exec.RunbookCallback;
import com.dc.runbook.rt.exec.RunbookItemStatus;
import com.dc.util.condition.ConditionalBarrier;

public class SampleRunBookCallBack implements RunbookCallback {

    private ConditionalBarrier<String> barrier;
    private String blockingId;
    private String outputData = "";

    public SampleRunBookCallBack(ConditionalBarrier<String> barrier, String blockingId) {
        this.barrier = barrier;
        this.blockingId = blockingId;
    }

    @Override
    public DtRunbook getRunbook() {
        return null;
    }

    @Override
    public RunbookItemStatus executingItem(DtRunbookItem runbookItem) {
        System.out.println("executingItem : " + runbookItem.getItemId());
        return null;
    }

    @Override
    public RunbookItemStatus completedItem(DtRunbookItem runbookItem) {
        System.out.println("completedItem : " + runbookItem.getItemId());
        return null;
    }

    @Override
    public RunbookItemStatus skippingItem(DtRunbookItem runbookItem) {
        return null;
    }

    @Override
    public RunbookItemStatus pausedItem(DtRunbookItem runbookItem) {
        return null;
    }

    @Override
    public RunbookItemStatus resumedItem(DtRunbookItem runbookItem) {
        return null;
    }

    @Override
    public void output(NodeOutputChunk nodeOutputChunk) {
        outputData += nodeOutputChunk.getOutputChunk();
        System.out.println(nodeOutputChunk.getOutputChunk());
    }

    @Override
    public void error(NodeOutputChunk nodeOutputChunk) {
        System.out.println(nodeOutputChunk.getOutputChunk());
    }

    @Override
    public void started() {

    }

    @Override
    public void markCancelled() {

    }

    @Override
    public void done() {
        System.out.println("Done");
        barrier.release(blockingId);
    }

    @Override
    public void done(Exception e) {
        System.out.println("Done");
        e.printStackTrace();
        barrier.release(blockingId);
    }

    @Override
    public String getExecutionId() {
        return null;
    }

    @Override
    public void itemExecOnNodeDone(String itemId, String nodeId, int statusCode, String message) {
        System.out.println("Item Exec Done on Node " + nodeId);
    }

    @Override
    public boolean didLatestStepFail() {
        return false;
    }

    public String getOutputData() {
        return outputData;
    }

}