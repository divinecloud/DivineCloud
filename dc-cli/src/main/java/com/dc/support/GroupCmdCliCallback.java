package com.dc.support;

import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.util.condition.BasicConditionalBarrier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupCmdCliCallback implements GroupTermCallback {

    private Map<String, String> outputMap;
    private Map<String, Integer> statusCodeMap;
    private BasicConditionalBarrier barrier;

    public GroupCmdCliCallback(BasicConditionalBarrier barrier) {
        this.barrier = barrier;
        outputMap = new ConcurrentHashMap<>();
    }

    @Override
    public void complete(String nodeDisplayId, int statusCode) {
        String result = outputMap.get(nodeDisplayId);
        System.out.println('\n' + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("Node : " + nodeDisplayId + "  |  STARTED");
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + '\n');
        System.out.println(result);
        System.out.println('\n' + "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        System.out.println("Node : " + nodeDisplayId + "  |  " + (statusCode == 0 ? "COMPLETE" : "FAILED | " + " code : " + statusCode));
        System.out.println("-----------------------------------------------------------------------------------" + '\n');

    }

    @Override
    public void output(String displayId, String output) {
        synchronized (displayId.intern()) {
            if(outputMap.get(displayId) == null) {
                outputMap.put(displayId, "");
            }
            outputMap.put(displayId, outputMap.get(displayId) + output);
        }
    }

    @Override
    public void error(String displayId, String error) {
        output(displayId, error);
    }

    @Override
    public void started() {

    }

    @Override
    public void markCancelled() {

    }

    @Override
    public void done() {
        barrier.release();
    }

    @Override
    public void done(Exception e) {
        barrier.release();
    }
}
