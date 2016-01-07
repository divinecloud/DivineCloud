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

package com.dc.runbook.dt.explorer;

import com.dc.runbook.RunBookException;
import com.dc.runbook.dt.domain.Location;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.domain.RunbookInfo;
import com.dc.runbook.dt.locator.RunBookLocator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RunBookExplorer {

    public static int count(File baseFolder) throws RunBookException {
        int result = 0;
        if(baseFolder == null || !baseFolder.exists()) {
            throw new RunBookException("Invalid path provided for the RunBook Root folder : " + ((baseFolder != null) ? baseFolder.getAbsolutePath():""));
        }
        File[] listOfFiles = baseFolder.listFiles();
        if(listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".runbook")) {
                        result++;
                    }
                } else if (file.isDirectory()) {
                    result += count(file);
                }
            }
        }
        return result;
    }

    public static int count(List<File> baseFoldersList) throws RunBookException {
        int result = 0;
        if(baseFoldersList == null || baseFoldersList.size() == 0) {
            throw new RunBookException("Empty RunBook Repository Folders provided.");
        }

        for(File baseFolder : baseFoldersList) {
            result += count(baseFolder);
        }
        return result;
    }

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

    public static RunbookInfo fetchRunbookInfo(String fullPath) throws RunBookException {
        if(fullPath == null) {
            throw new RunBookException("Invalid path provided for the RunBook : " + fullPath);
        }
        RunbookInfo runbookInfo = new RunbookInfo();
        RunBook runbook = RunBookLocator.locate(fullPath, Location.Local);
        runbookInfo.setName(runbook.getName());
        runbookInfo.setFullPath(fullPath);
        return runbookInfo;
    }
}
