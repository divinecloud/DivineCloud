package com.dc.support;

import com.dc.runbook.rt.cmd.exec.GroupTermCallback;
import com.dc.util.condition.BasicConditionalBarrier;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupCmdCliCallback implements GroupTermCallback {

    private Map<String, String> outputMap;
    private Map<String, Integer> statusCodeMap;
    private BasicConditionalBarrier barrier;
    private boolean storeOutput;
    private File destinationFile;

    public GroupCmdCliCallback(BasicConditionalBarrier barrier, File destinationFile) {
        this.barrier = barrier;
        this.destinationFile = destinationFile;

        if(destinationFile != null) {
            storeOutput = true;
        }

        outputMap = new ConcurrentHashMap<>();
        statusCodeMap = new ConcurrentHashMap<>();
    }

    @Override
    public void complete(String nodeDisplayId, int statusCode) {
        statusCodeMap.put(nodeDisplayId, statusCode);
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
        try {
            writeToFile();
        }
        finally {
            barrier.release();
        }
    }

    @Override
    public void done(Exception e) {
        try {
            writeToFile();
        }
        finally {
            barrier.release();
        }
    }

    private void writeToFile() {
        if(storeOutput) {
            ExecutionOutput executionOutput = new ExecutionOutput();
            executionOutput.setOutputMap(outputMap);
            executionOutput.setStatusCodeMap(statusCodeMap);
            ExecOutputFileStore store = new ExecOutputFileStore(executionOutput, destinationFile);
            store.store();
        }
    }
}
