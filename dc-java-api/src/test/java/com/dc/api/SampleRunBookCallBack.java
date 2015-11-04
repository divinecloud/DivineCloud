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