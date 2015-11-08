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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This class converts an error stack string to Java Object. The reason behind doing this is so that eventually the 'caused by' can be used as meta data for stats.
public class ErrorStack{
    private String message;
    private List<Object> stackTraceElements;
    private static Pattern AT_PATTERN = Pattern.compile("^at (.*)\\.(.*)\\((.*)\\)$");
    private String className;
    private ErrorStack currentStack;


    public ErrorStack(String tokens){
        if (tokens.contains(":")){
            String[] exceptionFragment = tokens.trim().split(":");
            this.className = exceptionFragment[0];
            message = getRemainingTokens(":", exceptionFragment, 1);
        } else{
            this.className = "Unknown";
            message = tokens;
        }
        stackTraceElements = new ArrayList<Object>();
        currentStack = this;
    }

    public String getClassName() {
        return className;
    }

    public void appendMessage(String line){
        currentStack = appendMessageInternal(currentStack, line);
    }

    public ErrorStack appendMessageInternal(ErrorStack stackObject, String line) {
        if (line.trim().startsWith("at ")) {
            Matcher matcher = AT_PATTERN.matcher(line.trim());
            if (matcher.matches()){
                String className = matcher.group(1);
                String methodName = matcher.group(2);
                String fileInfo = matcher.group(3);
                String fileName;
                int lineNumber;
                if (fileInfo.equals("Native Method")) {
                    fileName = null;
                    lineNumber = -2;
                } else if (fileInfo.equals("Unknown Source")) {
                    fileName = null;
                    lineNumber = -1;
                } else if (fileInfo.contains(":")) {
                    fileName = fileInfo.split(":")[0];
                    lineNumber = Integer.parseInt(fileInfo.split(":")[1]);
                } else {
                    fileName = fileInfo;
                    lineNumber = -1;
                }
                stackObject.stackTraceElements.add(new StackTraceElement(className, methodName, fileName, lineNumber));
                return stackObject;
            } else{
                throw new IllegalArgumentException("error: "+ line);
            }
        } else if (line.trim().startsWith("...")){
            stackObject.stackTraceElements.add(line);
            return stackObject;
        } else if (line.trim().startsWith("Caused by:")){
            String[] causedByTokens = line.split(":");
            ErrorStack cause = new ErrorStack(getRemainingTokens(":", causedByTokens, 1));
            stackObject.stackTraceElements.add(cause);
            return cause;
        } else {  //This is typically not part of message stack. TODO: Flag this as error? Some messages from iBates uses newline on message desc.
            stackObject.stackTraceElements.add(line);
            return stackObject;
        }
    }

    public static String getRemainingTokens(String seperator, String[] tokens, int pos) { //TODO: Move it to common utils.
        return getRemainingTokens(seperator, tokens, pos, tokens.length-1);
    }

    public static String getRemainingTokens(String seperator, String[] tokens, int start, int end) { //TODO: Move it to common utils.
        StringBuilder returnValue = new StringBuilder();
        for(int i=start; i <= end;i++){
            returnValue.append(tokens[i]).append(seperator);
        }
        int len = returnValue.length();
        return len > 0 ? returnValue.toString().substring(0, len-1): "";
    }

    public String getMessageStackAsString() {
        StringBuilder rtn = new StringBuilder();
        rtn.append(className).append(":").append(message).append("\n");
        for(Object stackTraceElt: stackTraceElements){
            if (stackTraceElt instanceof StackTraceElement){
                rtn.append("\tat ").append(stackTraceElt.toString()).append("\n");
            } else if (stackTraceElt instanceof String){
                rtn.append(stackTraceElt.toString()).append("\n");
            } else if (stackTraceElt instanceof ErrorStack){
                rtn.append("Caused by: ").append(((ErrorStack) stackTraceElt).getMessageStackAsString()).append("\n");
            }
        }
        return rtn.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorStack)) return false;

        ErrorStack that = (ErrorStack) o;

        if (!className.equals(that.className)) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (stackTraceElements.size() != that.stackTraceElements.size()) return false;
        for(int i=0; i < stackTraceElements.size(); i++){
            if ((stackTraceElements.get(i) instanceof StackTraceElement)){
                StackTraceElement thisStackTraceElement = (StackTraceElement) stackTraceElements.get(i);
                if (!(that.stackTraceElements.get(i) instanceof StackTraceElement)){
                     return false;
                }
                StackTraceElement thatStackTraceElement = (StackTraceElement) that.stackTraceElements.get(i);
                if (!equals(thisStackTraceElement, thatStackTraceElement)){
                    return false;
                }
            } else if (stackTraceElements.get(i) instanceof ErrorStack){
                ErrorStack thisErrorStackCause = (ErrorStack) stackTraceElements.get(i); 
                if (!(that.stackTraceElements.get(i) instanceof ErrorStack)){
                    return false;
                }
                ErrorStack thatErrorStackCause = (ErrorStack) that.stackTraceElements.get(i);
                if (!thisErrorStackCause.equals(thatErrorStackCause)){
                    return false;
                }
            }
            //Ignore other value types.
        }
        return true;
    }

    private boolean equals(StackTraceElement thisStackTraceElement, StackTraceElement thatStackTraceElement) {
        return thisStackTraceElement.equals(thatStackTraceElement);
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        for(Object o:stackTraceElements){
            if (o instanceof StackTraceElement || o instanceof ErrorStack){
                result = 31 * result + o.hashCode();
            }
            //Ignore other value types.
        }
        result = 31 * result + stackTraceElements.hashCode(); //This is not the most reliable way.
        result = 31 * result + className.hashCode();
        return result;
    }
}
