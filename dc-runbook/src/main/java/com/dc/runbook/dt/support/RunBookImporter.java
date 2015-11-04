package com.dc.runbook.dt.support;

import java.io.File;

import com.dc.runbook.dt.domain.Location;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.locator.RunBookLocator;
import com.dc.runbook.dt.yaml.RunBookWriter;

public class RunBookImporter {

    public static void httpImport(String uri, String target) {
        RunBook result = RunBookLocator.locate(uri, Location.Http);
        RunBookWriter.write(result, new File(target));
    }
}
