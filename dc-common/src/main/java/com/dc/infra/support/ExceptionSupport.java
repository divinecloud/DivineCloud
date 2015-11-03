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

package com.dc.infra.support;

/**
 * Support class providing exception related helpful methods commonly used by
 * different apps.
 * 
 */
public class ExceptionSupport {

    public static String getExceptionMessage(String message, Throwable error) {
        StringBuilder messageBuilder = new StringBuilder();
        if (message != null) {
            messageBuilder.append(message).append("\n");
        }
        messageBuilder = messageBuilder.append("\n");
        while (error != null) {
            messageBuilder.append(error.getMessage()).append("\n");
            StackTraceElement[] stackTraceElements = error.getStackTrace();
            for (StackTraceElement element : stackTraceElements) {
                messageBuilder.append(element.toString()).append("\n");
            }
            error = error.getCause();
        }
        return messageBuilder.toString();
    }

    public static String getExceptionMessage(Throwable error) {
        return getExceptionMessage(null, error);
    }

    public static String getExceptionMessageInHtmlFormat(Throwable error) {
        return getExceptionMessageInHtmlFormat(null, error);
    }

    public static String getExceptionMessageInHtmlFormat(String message, Throwable error) {
        StringBuilder messageBuilder = new StringBuilder();
        if (message != null) {
            messageBuilder.append(message).append("<br>");
        }
        messageBuilder = messageBuilder.append("<br>");
        while (error != null) {
            messageBuilder.append(error.getMessage()).append("<br>");
            StackTraceElement[] stackTraceElements = error.getStackTrace();
            for (StackTraceElement element : stackTraceElements) {
                messageBuilder.append(element.toString()).append("<br>");
            }
            error = error.getCause();
        }
        return messageBuilder.toString();
    }

}
