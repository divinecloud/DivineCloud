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

package com.dc;


import com.dc.api.ApiBuilder;
import com.dc.api.runbook.RunBookApi;

import java.io.File;

public class DivineCloudCli {

    public static void main(String [] args) {
        if(args == null || args.length < 5) {
            printMessage("Invalid arguments passed");
        }

        CliArgs cliArgs = parse(args);
        validateFile(cliArgs.runbookFile, "RunBook file path is invalid. ");
        execute(cliArgs);
    }

    private static void execute(CliArgs cliArgs) {
        RunBookApi api = ApiBuilder.buildRunBookApi(cliArgs.batchSize);
        api.execute(new File(cliArgs.nodesPerStepFile), new File(cliArgs.runbookFile), new File(cliArgs.outputFile),
                new File(cliArgs.credentialsProviderFile), new File(cliArgs.propertiesFile), true);
    }

    private static void validateFile(String path, String message) {
        if(path == null) {
            printMessage(message + " path is NULL");
        }
        else {
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                printMessage(message + " path : " + path);
            }
        }
    }

    private static void printMessage(String message) {
        System.out.println(message);
        System.out.println("");
        System.out.println("Usage: dc-cli -cmd <command-string> -n <nodes-per-step-file-path> [-b <batch-size>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -script <script-path> -n <nodes-per-step-file-path> [-a <arguments>] [-sudo y] [-b <batch-size>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -runbook <runBook-path> -n <nodes-per-step-file-path> [-p <properties-file-path>] [-c <credential-file-path>] [-o <output-file-path>] [-b <batch-size>]");
        System.exit(1);
    }

    private static CliArgs parse(String[] args) {
        CliArgs result = new CliArgs();
        int currentPointer = 0;
        while(currentPointer < args.length) {
            String type = args[currentPointer];
            switch(type) {
                case "-r" :
                    result.runbookFile = args[++currentPointer];
                    break;
                case "-n" :
                    result.nodesPerStepFile = args[++currentPointer];
                    break;
                case "-p" :
                    result.propertiesFile = args[++currentPointer];
                    break;
                case "-c" :
                    result.credentialsProviderFile = args[++currentPointer];
                    break;
                case "-o" :
                    result.outputFile = args[++currentPointer];
                    break;
                case "-b" :
                    try {
                        result.batchSize = Integer.parseInt(args[++currentPointer]);
                    }
                    catch(Exception e) {
                        printMessage("Invalid batch-size provided");
                    }

                default:
                    printMessage("Invalid argument specified : " + type);
            }
            ++currentPointer;

        }

        return result;
    }
}

class CliArgs {
    String nodesPerStepFile;
    String runbookFile;
    String outputFile;
    String credentialsProviderFile;
    String propertiesFile;
    boolean emitOutput;
    int batchSize = 100;


    @Override
    public String toString() {
        return "CliArgs{" +
                "nodesPerStepFile='" + nodesPerStepFile + '\'' +
                ", runbookFile='" + runbookFile + '\'' +
                ", outputFile='" + outputFile + '\'' +
                ", credentialsProviderFile='" + credentialsProviderFile + '\'' +
                ", propertiesFile='" + propertiesFile + '\'' +
                ", emitOutput=" + emitOutput +
                ", batchSize=" + batchSize +
                '}';
    }
}