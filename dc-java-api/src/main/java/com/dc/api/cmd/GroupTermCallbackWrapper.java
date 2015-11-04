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
