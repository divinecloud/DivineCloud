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
import com.dc.api.cmd.CmdApi;
import com.dc.api.cmd.CmdApiImpl;
import com.dc.api.runbook.RunBookApi;
import com.dc.api.support.ExecutionIdGenerator;
import com.dc.ssh.client.exec.cmd.script.ScriptCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.support.GroupCmdCliCallback;
import com.dc.support.KeyValuePair;
import com.dc.util.condition.BasicConditionalBarrier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DivineCloudCli {
    private static ExecutionIdGenerator idGenerator = new ExecutionIdGenerator();

    public static void main(String [] args) {
        if(args == null || args.length < 5) {
            printMessage("Invalid arguments passed");
        }
        execute(args);
    }

    private static void execute(String [] args) {

        CliArgs cliArgs = parse(args);

        if(cliArgs instanceof RunBookCliArgs) {
            executeRunBook((RunBookCliArgs)cliArgs);
        }
        else if(cliArgs instanceof ScriptCliArgs) {
            executeScript((ScriptCliArgs) cliArgs);
        }
        else {
            executeCmd((CmdCliArgs)cliArgs);
        }
    }

    private static void executeCmd(CmdCliArgs args) {
        CmdApi cmdApi = new CmdApiImpl(args.batchSize);
        String[] nodes = args.nodes;
        List<NodeCredentials> nodeCredentialsList = null;
        if(args.pwdFilePath != null) {
            nodeCredentialsList = convert(nodes, args.userName, args.pwdFilePath, false);
        }
        else {
            nodeCredentialsList = convert(nodes, args.userName, args.keyFilePath, true);
        }

        File outputFile = null;
        if(args.outputFile != null) {
            outputFile = new File(args.outputFile);
        }
        BasicConditionalBarrier barrier = new BasicConditionalBarrier();

        GroupCmdCliCallback callback = new GroupCmdCliCallback(barrier, outputFile);
        cmdApi.execute(nodeCredentialsList, args.cmd, callback);
        barrier.block();
    }

    private static List<NodeCredentials> convert(String[] nodes, String userName, String credFilePath, boolean keyBased) {
        List<NodeCredentials> result = null;
        if(nodes != null) {
            result = new ArrayList<>();
            byte[] keyBytes = null;
            try {
                keyBytes = Files.readAllBytes(Paths.get(credFilePath.trim()));
            } catch (IOException e) {
                printMessage("Error occurred while reading the credential file : " + credFilePath + " " + e.getMessage());
            }
            for(String node : nodes) {
                NodeCredentials credentials;
                if(keyBased) {
                    credentials = new NodeCredentials.Builder(node.trim(), userName.trim()).keySupport(true).privateKey(keyBytes).build();
                }
                else {
                    credentials = new NodeCredentials.Builder(node.trim(), userName.trim()).password(new String(keyBytes)).build();
                }
                result.add(credentials);
            }
        }
        return result;
    }

    private static void executeScript(ScriptCliArgs cliArgs) {
        CmdApi cmdApi = new CmdApiImpl(cliArgs.batchSize);
        String[] nodes = cliArgs.nodes;
        List<NodeCredentials> nodeCredentialsList = null;
        if(cliArgs.pwdFilePath != null) {
            nodeCredentialsList = convert(nodes, cliArgs.userName, cliArgs.pwdFilePath, false);
        }
        else {
            nodeCredentialsList = convert(nodes, cliArgs.userName, cliArgs.keyFilePath, true);
        }

        BasicConditionalBarrier barrier = new BasicConditionalBarrier();
        File outputFile = null;
        if(cliArgs.outputFile != null) {
            outputFile = new File(cliArgs.outputFile);
        }
        GroupCmdCliCallback callback = new GroupCmdCliCallback(barrier, outputFile);
        byte [] scriptBytes = null;
        try {
            scriptBytes = Files.readAllBytes(Paths.get(cliArgs.scriptFilePath.trim()));
        } catch (IOException e) {
            printMessage("Error occurred while reading script file content. " + cliArgs.scriptFilePath + " - " + e.getMessage());
        }
        ScriptCommand scriptCommand = new ScriptCommand(idGenerator.next(), new String(scriptBytes), ScriptLanguage.Shell, "/bin/sh");
        cmdApi.execute(nodeCredentialsList, scriptCommand, callback);
        barrier.block();
    }

    private static void executeRunBook(RunBookCliArgs cliArgs) {
        RunBookApi api = ApiBuilder.buildRunBookApi(cliArgs.batchSize);
        api.execute(new File(cliArgs.nodesFilePath), new File(cliArgs.runbookFile), new File(cliArgs.outputFile),
                new File(cliArgs.credentialsProviderFile), new File(cliArgs.propertiesFile), true);
    }

    private static boolean validateFile(String path, String message) {
        boolean valid = true;
        if(path == null) {
            valid = false;
            printMessage(message + " path is NULL");
        }
        else {
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                valid = false;
                printMessage(message + " path : " + path);
            }
        }
        return valid;
    }

    private static void printMessage(String message) {
        System.out.println(message);
        System.out.println("");
        System.out.println("Usage: dc-cli -cmd \"<command-string>\" -n <nodes-file-path> [-o <output-file-path>] [-b <batch-size>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -cmd \"<command-string>\" -nodes \"node1, node2, node3\" -user <username> [-key <key-file-path> | -pwd <pwd-file-path>] [-o <output-file-path>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -script <script-path> -n <nodes-file-path> [-b <batch-size>] [-a <arguments>] [-o <output-file-path>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -script <script-path> -user <username> [-key <key-file-path> | -pwd <pwd-file-path>] -nodes \"node1, node2, node3\" [-b <batch-size>] [-a <arguments>] [-o <output-file-path>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -runbook <runBook-path> -n <nodes-per-step-file-path> [-p <properties-file-path>] [-c <credential-file-path>] [-o <output-file-path>] [-b <batch-size>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -runbook <runBook-path> -user <username> [-key <key-file-path> | -pwd <pwd-file-path>] -nodes \"node1, node2, node3\" [-p <properties-file-path>] [-c <credential-file-path>] [-o <output-file-path>] [-b <batch-size>]");
        System.exit(1);
    }

    private static CliArgs parse(String[] args) {
        CliArgs result = null;
        String arguments = concat(args);

        KeyValuePair<String,String> nodesData = null;
        if(arguments.indexOf(" -nodes ") > 0) {
            nodesData = parseNodes(arguments);
        }

        String cmdString = arguments;
        if(nodesData != null) {
            cmdString = nodesData.getValue();
        }


        if(cmdString.contains("-runbook ")) {
            result = parseRunBook(cmdString.split(" "));
        }
        else if(cmdString.contains("-script ")) {
            KeyValuePair<String,String> argData;
            if(nodesData != null) {
                argData = parseArguments(cmdString);
            }
            else {
                argData = parseArguments(arguments);
            }

            if(argData.getValue() != null) {
                cmdString = argData.getValue();
            }
            result = parseScript(cmdString.split(" "));

            if(argData != null && argData.getKey() != null && argData.getKey().length() > 0) {
                ((ScriptCliArgs)result).arguments = argData.getKey();
            }

        }
        else if(cmdString.contains("-cmd ")) {
            KeyValuePair<String,String> cmdLinePair = parseCmdLine(cmdString);

            result = parseCmd(cmdLinePair);
        }
        else {
            printMessage("Invalid Arguments provided");
        }

        if(nodesData != null && nodesData.getKey() != null) {
            String[] nodes = nodesData.getKey().split(",");
            result.nodes = nodes;
        }
        return result;
    }

    private static KeyValuePair<String, String> parseCmdLine(String args) {
        KeyValuePair<String, String> result = new KeyValuePair<>();
        if(args.contains("-cmd ")) {
            int startIndex = args.indexOf("-cmd ");
            int nodesStartIndex = startIndex + 5;
            String subString = args.substring(nodesStartIndex);
            if(subString.trim().startsWith("\"")) {
                int doubleQuoteStart = findDoubleQuoteIndex(subString);
                String remainingSubString = subString.substring(doubleQuoteStart + 1);
                int doubleQuoteEnd = findDoubleQuoteIndex(remainingSubString);
                result.setKey(subString.substring(doubleQuoteStart + 1, doubleQuoteEnd + 1));
                String prunedString = args.substring(0, startIndex) + subString.substring(doubleQuoteEnd + 3);
                result.setValue(prunedString);

            }
            else {
                int wordEndIndex = subString.indexOf(" ");
                result.setKey(subString.substring(0, wordEndIndex));
                String prunedString = args.substring(0, startIndex) + subString.substring(wordEndIndex + 1);
                result.setValue(prunedString);
            }
        }

        return result;
    }

    private static CliArgs parseCmd(KeyValuePair<String, String> pair) {
        String[] args = pair.getValue().split(" ");
        String cmdLine = pair.getKey();
        CmdCliArgs result = new CmdCliArgs();
        result.cmd = cmdLine;
        int currentPointer = 0;
        while (currentPointer < args.length) {
            String type = args[currentPointer].trim();
            switch (type) {
                case "-user":
                    result.userName = args[++currentPointer];
                    break;
                case "-key":
                    result.keyFilePath = args[++currentPointer];
                    break;
                case "-pwd":
                    result.pwdFilePath = args[++currentPointer];
                    break;
                case "-n":
                    result.nodesFilePath = args[++currentPointer];
                    break;
                case "-o":
                    result.outputFile = args[++currentPointer];
                    break;
                case "-b":
                    try {
                        result.batchSize = Integer.parseInt(args[++currentPointer]);
                    } catch (Exception e) {
                        printMessage("Invalid batch-size provided");
                    }
                    break;
                case "":
                    break;
                default:
                    System.out.println("warning: " + type + " not recognized");
            }
            ++currentPointer;

        }
        if(result.pwdFilePath != null) {
            validateFile(result.pwdFilePath, "Password file path is invalid. ");
        }
        else {
            validateFile(result.keyFilePath, "Private Key file path is invalid. ");
        }
        return result;

    }

    private static CliArgs parseScript(String[] args) {
        ScriptCliArgs result = new ScriptCliArgs();
        int currentPointer = 0;
        while (currentPointer < args.length) {
            String type = args[currentPointer].trim();
            switch (type) {
                case "-script":
                    result.scriptFilePath = args[++currentPointer];
                    break;
                case "-user":
                    result.userName = args[++currentPointer];
                    break;
                case "-key":
                    result.keyFilePath = args[++currentPointer];
                    break;
                case "-pwd":
                    result.pwdFilePath = args[++currentPointer];
                    break;
                case "-n":
                    result.nodesFilePath = args[++currentPointer];
                    break;
                case "-p":
                    result.propertiesFile = args[++currentPointer];
                    break;
                case "-o":
                    result.outputFile = args[++currentPointer];
                    break;
                case "-b":
                    try {
                        result.batchSize = Integer.parseInt(args[++currentPointer]);
                    } catch (Exception e) {
                        printMessage("Invalid batch-size provided");
                    }
                    break;
                case "":
                    break;
                default:
                    printMessage("Invalid argument specified : " + type);
            }
            ++currentPointer;

        }

        validateFile(result.scriptFilePath, "Script file path is invalid. ");
        return result;
    }

    private static String concat(String[] args) {
        String result = "";
        for(String arg : args) {
            if(result.equals("")) {
                result = arg;
            }
            else {
                result = result + " " + arg;
            }
        }
        return result;
    }

    private static RunBookCliArgs parseRunBook(String[] args) {
        RunBookCliArgs result = new RunBookCliArgs();
        int currentPointer = 0;
        while(currentPointer < args.length) {
            String type = args[currentPointer].trim();
            switch(type) {
                case "-runbook" :
                    result.runbookFile = args[++currentPointer];
                    break;
                case "-user":
                    result.userName = args[++currentPointer];
                    break;
                case "-key":
                    result.keyFilePath = args[++currentPointer];
                    break;
                case "-pwd":
                    result.pwdFilePath = args[++currentPointer];
                    break;
                case "-n" :
                    result.nodesFilePath = args[++currentPointer];
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
                case "" :
                    break;
                default:
                    printMessage("Invalid argument specified : " + type);
            }
            ++currentPointer;

        }

        validateFile(result.runbookFile, "RunBook file path is invalid. ");

        return result;
    }

    private static boolean isInline(String[] args) {
        boolean result = false;
        for(String arg : args) {
            if(arg.contains("-nodes ")) {
                result = true;
                break;
            }
        }
        return result;
    }


    public static KeyValuePair<String,String> parseNodes(String args) {
        KeyValuePair<String,String> result = new KeyValuePair<>();
        if(args.contains("-nodes ")) {
            int startIndex = args.indexOf("-nodes ");
            int nodesStartIndex = startIndex + 7;
            String subString = args.substring(nodesStartIndex);
            int doubleQuoteStart = subString.indexOf("\"");
            String remainingSubString = subString.substring(doubleQuoteStart + 1);
            int doubleQuoteEnd = remainingSubString.indexOf("\"");
            result.setKey(subString.substring(doubleQuoteStart + 1, doubleQuoteEnd + 1));
            String prunedString = args.substring(0, startIndex) + subString.substring(doubleQuoteEnd + 3);
            result.setValue(prunedString);
        }

        return result;
    }

    public static KeyValuePair<String,String> parseArguments(String args) {
        KeyValuePair<String,String> result = new KeyValuePair<>();
        if(args.contains(" -a ")) {
            int startIndex = args.indexOf(" -a ");
            int nodesStartIndex = startIndex + 4;
            String subString = args.substring(nodesStartIndex);
            int doubleQuoteStart = findDoubleQuoteIndex(subString);
            String remainingSubString = subString.substring(doubleQuoteStart + 1);
            int doubleQuoteEnd = findDoubleQuoteIndex(remainingSubString);
            result.setKey(subString.substring(doubleQuoteStart + 1, doubleQuoteEnd + 4));
            String prunedString = args.substring(0, startIndex) + subString.substring(doubleQuoteEnd + 5);
            result.setValue(prunedString);
        }

        return result;
    }

    private static int findDoubleQuoteIndex(String line) {
        int doubleQuoteIndex = line.indexOf("\"");
        if(doubleQuoteIndex > 0) {
            if(line.charAt(doubleQuoteIndex - 1) == '\\') {
                doubleQuoteIndex += findDoubleQuoteIndex(line.substring(doubleQuoteIndex + 1));
            }
        }
        return doubleQuoteIndex;
    }
}

class CliArgs {
    String outputFile;
    String propertiesFile;
    String nodesFilePath;
    boolean emitOutput;
    int batchSize = 100;
    String[] nodes;
    String keyFilePath;
    String pwdFilePath;
    String userName;
}

class CmdCliArgs extends CliArgs {
    String cmd;
}

class ScriptCliArgs extends CliArgs {
    String arguments;
    String scriptFilePath;
}

class RunBookCliArgs extends CliArgs {
    String runbookFile;
    String credentialsProviderFile;
}

