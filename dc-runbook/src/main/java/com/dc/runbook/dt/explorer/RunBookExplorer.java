package com.dc.runbook.dt.explorer;

import com.dc.runbook.RunBookException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RunBookExplorer {

    public static List<String> listRunBooks(File baseFolder) throws RunBookException {
        if(baseFolder == null || !baseFolder.exists()) {
            throw new RunBookException("Invalid path provided for the RunBook Root folder : " + ((baseFolder != null) ? baseFolder.getAbsolutePath():""));
        }

        List<String> result = new ArrayList<>();
        listFiles(baseFolder, result);
        return result;
    }

    private static void listFiles(File folder, List<String> result) {
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
//                    if (file.getName().endsWith(".runbook")) {
                        result.add(file.getAbsolutePath());
//                    }
                } else if (file.isDirectory()) {
                    listFiles(file, result); //@TODO: Later add logic to capture Permission Denied exceptions and move on
                }
            }
        }
    }
}
