package com.dc.api;

import com.dc.api.cmd.CmdApi;
import com.dc.api.cmd.CmdApiImpl;
import com.dc.api.runbook.RunBookApi;
import com.dc.api.runbook.RunBookApiImpl;

public class ApiBuilder {

    public static CmdApi buildCmdApi(int batchSize) {
        return new CmdApiImpl(batchSize);
    }

    public static RunBookApi buildRunBookApi(int batchSize) {
        return new RunBookApiImpl(batchSize);
    }

}
