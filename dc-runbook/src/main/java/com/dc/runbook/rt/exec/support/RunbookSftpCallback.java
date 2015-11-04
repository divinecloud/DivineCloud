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

package com.dc.runbook.rt.exec.support;


import com.dc.runbook.rt.exec.RunbookCallback;
import com.dc.ssh.client.sftp.SftpCallback;
import com.dc.ssh.client.sftp.SftpClientException;
import com.dc.util.condition.ConditionalBarrier;

public class RunbookSftpCallback implements SftpCallback {

    private RunbookCallback callback;
    private String	                   displayId;
    private String	                       runbookItemId;
    private ConditionalBarrier<String> conditionalBarrier;

    private int	                       statusCode	= 0;
    private SftpClientException cause;

    public RunbookSftpCallback(RunbookCallback callback, String displayId, String runbookItemId, ConditionalBarrier<String> conditionalBarrier) {
        this.callback = callback;
        this.displayId = displayId;
        this.runbookItemId = runbookItemId;
        this.conditionalBarrier = conditionalBarrier;
    }

    public void done() {
        callback.itemExecOnNodeDone(runbookItemId, displayId, statusCode, null);
        conditionalBarrier.release(displayId + "_" + runbookItemId);

    }

    @Override
    public void done(SftpClientException cause) {
        statusCode = 999;
        cause.printStackTrace();
        this.cause = cause;
        callback.itemExecOnNodeDone(runbookItemId, displayId, 999, cause.getMessage());
        conditionalBarrier.release(displayId + "_" + runbookItemId);

    }


    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public int precentageComplete(int count) {
        return 0;
    }

    @Override
    public void execId(String processId) {

    }

    @Override
    public SftpClientException getCause() {
        return cause;
    }
}
