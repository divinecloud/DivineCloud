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

package com.dc.runbook.rt.cmd.exec;

import com.dc.runbook.rt.exec.support.ColorCodeFilter;

public class GroupTermCallbackImpl implements GroupTermCallback {

    private String statusKey;
    private String execId;
    private GroupTermOutputTransmitter outputTransmitter;
    private GroupTermExecStatus groupTermExecStatus;
    private ExecutionCompleteEvent executionCompleteEvent;
    private volatile boolean cancelled;

    public GroupTermCallbackImpl(String statusKey, String execId, GroupTermOutputTransmitter outputTransmitter, GroupTermExecStatus groupTermExecStatus, ExecutionCompleteEvent executionCompleteEvent) {
        this.statusKey = statusKey;
        this.execId = execId;
        this.outputTransmitter = outputTransmitter;
        this.groupTermExecStatus = groupTermExecStatus;
        this.executionCompleteEvent = executionCompleteEvent;
    }

    @Override
    public void complete(String nodeDisplayId, int statusCode) {
        GroupCmdExecState state;
        if(statusCode == 0) {
            groupTermExecStatus.successfull(nodeDisplayId);
            state = GroupCmdExecState.SUCCESSFUL;
        }
        else if(cancelled) {
            groupTermExecStatus.failed(nodeDisplayId);
            state = GroupCmdExecState.CANCELLED;
        }
        else {
            groupTermExecStatus.failed(nodeDisplayId);
            state = GroupCmdExecState.FAILED;
        }
        boolean complete = groupTermExecStatus.verifyIfComplete();
        GroupTermExecStatusChunk outputChunk = createOutputChunk(nodeDisplayId, state, false);
        outputTransmitter.transmit(outputChunk);
        if(complete) {
            System.out.println(groupTermExecStatus);
            done();
        }
    }

    private GroupTermExecStatusChunk createOutputChunk(String nodeDisplayId, GroupCmdExecState state, boolean error) {
        return createOutputChunk(nodeDisplayId, state, "", error);
    }

    private GroupTermExecStatusChunk createOutputChunk(String nodeDisplayId, GroupCmdExecState state, String output, boolean error) {
        GroupTermExecStatus s = groupTermExecStatus;
        boolean complete = s.isComplete();
        GroupCmdExecState groupCmdExecState = s.getState();
        return new GroupTermExecStatusChunk(execId, s.getSuccessCount(), s.getFailedCount(), s.getTotalCount(), groupCmdExecState, complete, nodeDisplayId, output, state, error, groupTermExecStatus.getStartTime());
    }

    @Override
    public void output(String nodeDisplayId, String output) {
        output = ColorCodeFilter.filter(output);
        groupTermExecStatus.addOutput(nodeDisplayId, output, false);
        GroupTermExecStatusChunk outputChunk = createOutputChunk(nodeDisplayId, GroupCmdExecState.RUNNING, output, false);
        outputTransmitter.transmit(outputChunk);
    }

    @Override
    public void error(String nodeDisplayId, String error) {
        error = ColorCodeFilter.filter(error);
        groupTermExecStatus.addOutput(nodeDisplayId, error, true);
        GroupTermExecStatusChunk outputChunk = createOutputChunk(nodeDisplayId, GroupCmdExecState.RUNNING, error, true);
        outputTransmitter.transmit(outputChunk);
    }

    @Override
    public void started() {
        groupTermExecStatus.setStartTime(System.currentTimeMillis());
        GroupTermExecStatusChunk outputChunk = createOutputChunk("", GroupCmdExecState.RUNNING, "", false);
        outputTransmitter.transmit(outputChunk);
    }

    @Override
    public void markCancelled() {
        cancelled = true;
        groupTermExecStatus.setCancelled(true);
    }

    @Override
    public void done() {
        executionCompleteEvent.complete(statusKey, groupTermExecStatus);
    }

    @Override
    public void done(Exception e) {
        executionCompleteEvent.complete(statusKey, groupTermExecStatus);
    }


    public String getExecutionId() {
        return execId;
    }


    public GroupTermExecStatus getExecStatus() {
        return groupTermExecStatus;
    }



}
