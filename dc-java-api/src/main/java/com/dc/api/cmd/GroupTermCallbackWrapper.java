/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc.api.cmd;

import com.dc.runbook.rt.cmd.exec.GroupTermCallback;

import java.util.concurrent.atomic.AtomicInteger;

public class GroupTermCallbackWrapper implements GroupTermCallback {
    private GroupTermCallback callback;
    private AtomicInteger totalNodes;

    public GroupTermCallbackWrapper(GroupTermCallback callback, int totalNodesCount) {
        this.callback = callback;
        totalNodes = new AtomicInteger(totalNodesCount);
    }

    @Override
    public void complete(String nodeDisplayId, int statusCode) {
        try {
            callback.complete(nodeDisplayId, statusCode);
        }
        finally {
            int remaining = totalNodes.decrementAndGet();
            if (remaining == 0) {
                done();
            }
        }
    }

    @Override
    public void output(String displayId, String output) {
        callback.output(displayId, output);
    }

    @Override
    public void error(String displayId, String error) {
        callback.error(displayId, error);
    }

    @Override
    public void started() {
        callback.started();
    }

    @Override
    public void markCancelled() {
        callback.started();
    }

    @Override
    public void done() {
        callback.done();
    }

    @Override
    public void done(Exception e) {
        callback.done(e);
    }
}
