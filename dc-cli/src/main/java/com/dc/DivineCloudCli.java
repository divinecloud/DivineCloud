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

public class DivineCloudCli {

    public static void main(String [] args) {
        if(args == null || args.length < 2) {
            System.out.println("Invalid arguments passed.");
            System.out.println("Usage: ");
            System.out.println("dc-cli -r runbookFilePath -n nodesPerStepFilePath -p propertiesFilePath -c credentialProviderFilePath -o outputFilePath");
            System.exit(1);
        }

        CliArgs cliArgs = parse(args);

        System.out.println(cliArgs);
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

                default:
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


    @Override
    public String toString() {
        return "CliArgs{" +
                "nodesPerStepFile='" + nodesPerStepFile + '\'' +
                ", runbookFile='" + runbookFile + '\'' +
                ", outputFile='" + outputFile + '\'' +
                ", credentialsProviderFile='" + credentialsProviderFile + '\'' +
                ", propertiesFile='" + propertiesFile + '\'' +
                ", emitOutput=" + emitOutput +
                '}';
    }
}