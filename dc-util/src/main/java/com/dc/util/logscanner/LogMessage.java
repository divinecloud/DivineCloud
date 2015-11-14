
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//The class that represents a log message.
public class LogMessage {
    private String rootExceptionClass;
    private LogMessageType messageType;
    private List<MessageWrapper> messageWrappers;

    public static class Builder{
        private LogMessageType messageType;
        private String timeStamp;
        private ErrorStack errorStack;

        public Builder(){
        }
        
        public Builder parseLog4JMessage(String firstLine){
            String[] tokens = firstLine.split("\\|");
            setTimeStamp(tokens[0]);
            setMessageType(tokens[2]);
            assert messageType == LogMessageType.ERROR;
            errorStack = new ErrorStack(ErrorStack.getRemainingTokens("|", tokens, 10));
            return this;
        }

        public Builder parseTomcatMessage(String firstLine){
            setTimeStamp("Unknown");
            messageType = LogMessageType.ERROR;
            errorStack = new ErrorStack(firstLine);
            return this;
        }

        public Builder setTimeStamp(String timeStamp){
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder setMessageType(String messageType){
            this.messageType = LogMessageType.getLogMessageType(messageType);
            return this;
        }
        
        public Builder appendToLog(String line){
            errorStack.appendMessage(line);
            return this;
        }

        public LogMessageType getMessageType() {
            return messageType;
        }

        public ErrorStack getErrorStack() {
            return errorStack;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public LogMessage build(){
            return new LogMessage(this);
        }
    }

    public String getRootExceptionClass() {
        return rootExceptionClass;
    }

    public LogMessageType getMessageType() {
        return messageType;
    }

    public List<MessageWrapper> getMessageWrappers() {
        return Collections.unmodifiableList(messageWrappers);
    }

    public void addMessageWrappers(MessageWrapper wrapper) {
        messageWrappers.add(wrapper);
    }

    public static boolean isStartOfLog4JErrorStack(String line) {
        String[] tokens = line.split("\\|");
        return tokens.length >= 9;
    }

    private LogMessage(Builder builder) {
        this.messageType= builder.messageType;
        this.rootExceptionClass = builder.errorStack.getClassName();
        this.messageWrappers = new ArrayList<MessageWrapper>();
        this.messageWrappers.add(new MessageWrapper(builder.timeStamp, builder.errorStack));
    }

}
