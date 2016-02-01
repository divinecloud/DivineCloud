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
import com.dc.ssh.client.support.NodeCredentialsFileParser;
import com.dc.support.GroupCmdCliCallback;
import com.dc.util.condition.BasicConditionalBarrier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DivineCloudCli {
    private static ExecutionIdGenerator idGenerator = new ExecutionIdGenerator();

    public static void main(String [] args) {
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
        List<NodeCredentials> nodeCredentialsList;
        if(args.pwdFilePath != null) {
            nodeCredentialsList = convert(nodes, args.userName, args.pwdFilePath, false);
        }
        else if(args.keyFilePath != null) {
            nodeCredentialsList = convert(nodes, args.userName, args.keyFilePath, true);
        }
        else {
            String nodesCredText;
            try {
                nodesCredText = new String(Files.readAllBytes(Paths.get(args.nodesFilePath)));
            } catch (IOException e) {
                throw new DcException("Cannot read Nodes Cred File : " + args.nodesFilePath, e);
            }
            List<List<NodeCredentials>> nodesPerStep = NodeCredentialsFileParser.parse(nodesCredText, false);
            nodeCredentialsList = nodesPerStep.get(0);
        }
        File outputFile = null;
        if(args.outputFile != null) {
            outputFile = new File(args.outputFile);
        }
        BasicConditionalBarrier barrier = new BasicConditionalBarrier();

        GroupCmdCliCallback callback = new GroupCmdCliCallback(barrier, outputFile);
        cmdApi.execute(nodeCredentialsList, args.cmd, callback);
        barrier.block();
        cmdApi.close();
    }

    private static List<NodeCredentials> convert(String[] nodes, String userName, String credFilePath, boolean keyBased) {
        List<NodeCredentials> result = null;
        if(nodes != null) {
            result = new ArrayList<>();
            byte[] keyBytes;
            try {
                keyBytes = Files.readAllBytes(Paths.get(credFilePath.trim()));
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
            } catch (IOException e) {
                printMessage("Error occurred while reading the credential file : " + credFilePath + " " + e.getMessage());
            }
        }
        return result;
    }

    private static void executeScript(ScriptCliArgs cliArgs) {
        CmdApi cmdApi = new CmdApiImpl(cliArgs.batchSize);
        String[] nodes = cliArgs.nodes;
        List<NodeCredentials> nodeCredentialsList;
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
        byte [] scriptBytes;
        ScriptCommand scriptCommand;
        try {
            scriptBytes = Files.readAllBytes(Paths.get(cliArgs.scriptFilePath.trim()));
            scriptCommand = new ScriptCommand(idGenerator.next(), new String(scriptBytes), ScriptLanguage.Shell, "/bin/sh");
            cmdApi.execute(nodeCredentialsList, scriptCommand, callback);
            barrier.block();
        } catch (IOException e) {
            printMessage("Error occurred while reading script file content. " + cliArgs.scriptFilePath + " - " + e.getMessage());
        }
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
        System.out.println("Usage: dc-cli -cmd \"<command-string>\" -nodes node1,node2,node3 -user <username> [-key <key-file-path> | -pwd <pwd-file-path>] [-o <output-file-path>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -script <script-path> -n <nodes-file-path> [-b <batch-size>] [-a <arguments>] [-o <output-file-path>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -script <script-path> -user <username> [-key <key-file-path> | -pwd <pwd-file-path>] -nodes node1,node2,node3 [-b <batch-size>] [-a <arguments>] [-o <output-file-path>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -runbook <runBook-path> -n <nodes-per-step-file-path> [-p <properties-file-path>] [-c <credential-file-path>] [-o <output-file-path>] [-b <batch-size>]");
        System.out.println("        or");
        System.out.println("Usage: dc-cli -runbook <runBook-path> -user <username> [-key <key-file-path> | -pwd <pwd-file-path>] -nodes \"node1, node2, node3\" [-p <properties-file-path>] [-c <credential-file-path>] [-o <output-file-path>] [-b <batch-size>]");
        System.exit(1);
    }

    private static CliArgs parse(String[] args) {
        CliArgs result = null;

        Map<String, String> argsMap = convertToMap(args);

        if(argsMap.containsKey("-help")) {
            printMessage("DivineCloud CLI");
            System.exit(0);
        }

        if(argsMap.containsKey("-runbook")) {
            result = parseRunBook(argsMap);
        }
        else if(argsMap.containsKey("-script")) {
            result = parseScript(argsMap);
        }
        else if(argsMap.containsKey("-cmd")) {
            result = parseCmd(argsMap);
        }
        else {
            printMessage("Invalid Arguments provided");
        }
        return result;
    }


    private static Map<String, String> convertToMap(String [] args) {
        Map<String, String> result = new HashMap<>();
        int size = args.length;
        int currentIndex = 0;
        while(currentIndex < size) {
            String key = args[currentIndex++];
            String value = null;
            if(currentIndex < size) {
                value = args[currentIndex++];
            }
            result.put(key, value);
        }
        return result;
    }

    private static String trim(String str) {
        String result = str;
        if(str != null) {
            result = str.trim();
        }
        return result;
    }

    private static void parseCliArguments(CliArgs result, Map<String, String> argsMap) {
        result.userName = trim(argsMap.get("-user"));
        result.keyFilePath = trim(argsMap.get("-key"));
        result.pwdFilePath = trim(argsMap.get("-pwd"));
        result.nodesFilePath = trim(argsMap.get("-n"));
        result.propertiesFile = trim(argsMap.get("-p"));
        result.outputFile = trim(argsMap.get("-o"));
        try {
            String batchSizeStr = trim(argsMap.get("-b"));
            if(batchSizeStr != null && batchSizeStr.trim().length() > 0) {
                result.batchSize = Integer.parseInt(batchSizeStr);
            }
            else {
                result.batchSize = 5;
            }
        }
        catch(Exception e) {
            printMessage("Invalid batch-size provided");
        }

        if(result.pwdFilePath != null) {
            validateFile(result.pwdFilePath, "Password file path is invalid. ");
        }
        else {
            validateFile(result.keyFilePath, "Private Key file path is invalid. ");
        }

        String nodesStr = argsMap.get("-nodes");
        if(nodesStr != null && nodesStr.trim().length() > 0) {
            String [] args = nodesStr.split(",");
            String [] nodes = null;
            if(args != null) {
                nodes = new String[args.length];
                for(int i=0; i<args.length; i++) {
                    nodes[i] = trim(args[i]);
                }
            }
            result.nodes = nodes;
        }
    }

    private static RunBookCliArgs parseRunBook(Map<String, String> argsMap) {
        RunBookCliArgs result = new RunBookCliArgs();
        parseCliArguments(result, argsMap);
        result.runbookFile = argsMap.get("-runbook");
        result.credentialsProviderFile = argsMap.get("-c");
        validateFile(result.runbookFile, "RunBook file path is invalid. ");
        return result;
    }

    private static ScriptCliArgs parseScript(Map<String, String> argsMap) {
        ScriptCliArgs result = new ScriptCliArgs();
        parseCliArguments(result, argsMap);
        result.scriptFilePath = argsMap.get("-script");
        result.arguments = argsMap.get("-a");
        validateFile(result.scriptFilePath, "Script file path is invalid. ");
        return result;
    }

    private static CmdCliArgs parseCmd(Map<String, String> argsMap) {
        CmdCliArgs result = new CmdCliArgs();
        parseCliArguments(result, argsMap);
        result.cmd = argsMap.get("-cmd");
        return result;
    }

}

class CliArgs {
    String outputFile;
    String propertiesFile;
    String nodesFilePath;
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

