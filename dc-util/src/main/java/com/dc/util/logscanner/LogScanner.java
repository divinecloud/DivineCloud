
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

package com.dc.util.logscanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LogScanner {

    public Map<String, Integer> scan(List<File> logFiles, File outputFile, boolean verbose) throws IOException {
        if (logFiles == null) {
            throw new IllegalArgumentException("logFiles cannot be null");
        }
        Set<LogMessage> logMessages = createLogMessages(logFiles);
        if (verbose) {
            if (outputFile == null) {
                throw new IllegalArgumentException("output file cannot be null in verbose mode");
            }
            if (outputFile.exists() && outputFile.isDirectory()) {
                throw new IllegalArgumentException("Output file cannot be a folder");
            }
            scanVerbose(logMessages, outputFile);
        }
        return scanSimple(logMessages);
    }

    private void scanVerbose(Set<LogMessage> logMessages, File outputFile) throws IOException {
        Set<LogMessage> filteredLogMessages = new HashSet<LogMessage>();
        for (LogMessage logMessage : logMessages) {
            if (logMessage.getMessageType() != LogMessageType.ERROR)
                continue; //Ignore non-error messages;
            filteredLogMessages.add(logMessage);
        }
        writeToFile(filteredLogMessages, outputFile);
    }

    private Map<String, Integer> scanSimple(Set<LogMessage> logMessages) {
        Map<String, Integer> errorCountMap = new HashMap<String, Integer>();
        for (LogMessage logMessage : logMessages) {
            if (logMessage.getMessageType() != LogMessageType.ERROR)
                continue; //Ignore non-error messages;
            String exceptionClassName = logMessage.getRootExceptionClass();
            errorCountMap.put(exceptionClassName, logMessage.getMessageWrappers().size());
        }
        return errorCountMap;
    }

    private void writeToFile(Set<LogMessage> logMessages, File outputFile) throws IOException {
        FileWriter fw = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fw);
        for (LogMessage logMessage : logMessages) {
            bufferedWriter.newLine();
            bufferedWriter.write("Exception class: " + logMessage.getRootExceptionClass());
            bufferedWriter.newLine();
            bufferedWriter.write("Number of occurrences: " + logMessage.getMessageWrappers().size());
            bufferedWriter.newLine();
            bufferedWriter.write("Occurrence in time: ");
            for (MessageWrapper wrapper : logMessage.getMessageWrappers()) {
                bufferedWriter.write(wrapper.getTimestamp() + ", ");
            }
            bufferedWriter.newLine();
            bufferedWriter.write("Stack: ");
            Set<ErrorStack> visitedStacks = new HashSet<ErrorStack>();
            for (MessageWrapper wrapper : logMessage.getMessageWrappers()) {
                if (!visitedStacks.contains(wrapper.getErrorStack())) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(wrapper.getErrorStack().getMessageStackAsString());
                    visitedStacks.add(wrapper.getErrorStack());
                }
            }
            bufferedWriter.write("----------------------------------------------------------------------------------------------------------");
        }
        bufferedWriter.close();
        fw.close();
    }

    private Set<LogMessage> createLogMessages(List<File> logFiles) throws IOException {
        Map<String, LogMessage> logMessagesMap = new LinkedHashMap<String, LogMessage>();
        for (File logFile : logFiles) {
            MessageBuffer messageBuffer = new MessageBuffer();
            assert logFile.exists();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
            String line;
            //LogMessage.Builder messageBuilder = new LogMessage.Builder();
            MessageBuffer.Message message = new MessageBuffer.Message();
            while ((line = bufferedReader.readLine()) != null) {
                if (isExceptionLineEntry(line)) { //Possibly an exception from the previous line.
                    MessageBuffer.Message prevMessageEntryMessageObj = message;
                    if (!message.isEmpty()) {
                        String lastMessageEntry = prevMessageEntryMessageObj.getEntry(prevMessageEntryMessageObj.getEntryCount() - 1);
                        if (isExceptionLineEntry(lastMessageEntry.trim()) || LogMessage.isStartOfLog4JErrorStack(lastMessageEntry.trim())) {
                            //Nothing to do.
                        } else if (isExceptionStartLineEntry(lastMessageEntry.trim())) { //Exception statement from last entry and the current entry uses "at/ caused by".
                            message.removeLastLine();
                            messageBuffer.addMessage(message);
                            message = new MessageBuffer.Message();
                            message.addLine(lastMessageEntry);
                        }
                    }
                } else if (LogMessage.isStartOfLog4JErrorStack(line)) { //log4j message or empty newline.
                    if (!message.isEmpty()) {
                        messageBuffer.addMessage(message);
                        message = new MessageBuffer.Message();
                    }
                }
                message.addLine(line);
            }
            bufferedReader.close();
            if (!message.isEmpty()) {
                messageBuffer.addMessage(message);
            }
            writeBufferToLogMessageMap(messageBuffer, logMessagesMap);
        }
        return new HashSet<LogMessage>(logMessagesMap.values());
    }

    private static Pattern EXCEPTION_START_PATTERN = Pattern.compile("^(.*\\.)+(.*)\\: (.*)$");

    private boolean isExceptionStartLineEntry(String line) {
        Matcher matcher = EXCEPTION_START_PATTERN.matcher(line);
        return matcher.matches();
    }

    private boolean isExceptionLineEntry(String line) {
        return (line.trim().startsWith("at ") || line.trim().startsWith("Caused by: "));
    }

    private void writeBufferToLogMessageMap(MessageBuffer messageBuffer, Map<String, LogMessage> logMessagesMap) {
        for (MessageBuffer.Message message : messageBuffer.getMessages()) {
            if (message.isEmpty()) {
                continue;
            }
            LogMessage.Builder errorLogBuilder = new LogMessage.Builder();
            String entry = message.getEntry(0);
            if (LogMessage.isStartOfLog4JErrorStack(entry)) {
                errorLogBuilder.parseLog4JMessage(entry);
                if (errorLogBuilder.getMessageType() != LogMessageType.ERROR) {
                    continue;
                }
                for (int i = 1; i < message.getEntryCount(); i++) {
                    errorLogBuilder.appendToLog(message.getEntry(i));
                }
            } else {
                int index = getStackTraceHintIndex(message);
                if (index == -1) {
                    continue;
                }
                errorLogBuilder.parseTomcatMessage(ErrorStack.getRemainingTokens("\n", message.getEntries(), 0, index - 1));
                for (int i = index; i < message.getEntryCount(); i++) {
                    errorLogBuilder.appendToLog(message.getEntry(i));
                }
            }
            if (logMessagesMap.containsKey(errorLogBuilder.getErrorStack().getClassName())) {
                logMessagesMap.get(errorLogBuilder.getErrorStack().getClassName()).addMessageWrappers(new MessageWrapper(errorLogBuilder.getTimeStamp(), errorLogBuilder.getErrorStack()));
            } else {
                logMessagesMap.put(errorLogBuilder.getErrorStack().getClassName(), errorLogBuilder.build());
            }
        }
    }

    private int getStackTraceHintIndex(MessageBuffer.Message message) {

        for (int i = 0; i < message.getEntryCount(); i++) {
            String line = message.getEntry(i).trim();
            if (line.startsWith("at ") || line.startsWith("Caused by: ")) { //This is an exception stack not from log4j.
                return i;
            }
        }
        return -1;
    }

    public static void main(String args[]) {
        LogScanner scanner = new LogScanner();
        File logFile = new File(args[0]);
        File output = new File(args[1]);
        try {
            scanner.scan(Arrays.asList(logFile), output, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
