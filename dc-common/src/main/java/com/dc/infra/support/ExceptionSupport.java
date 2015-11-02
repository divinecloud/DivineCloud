/* *******************************************************************************
 *  Copyright 2011 Divine Cloud Inc.  All Rights Reserved.                       *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.                *
 *                                                                               *
 *  This code is property of Divine Cloud software (www.divinecloud.com),        *
 *  and cannot be used without valid license purchase.                           *
 *  Any part of code cannot be modified or distributed to others without the     *
 *  written permission from Divine Cloud.                                        *
 *                                                                               *
 *  This code is provided in the hope that it will benefit the user, but         *
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY   *
 *  or FITNESS FOR A PARTICULAR PURPOSE. Divine Cloud is not liable for any      *
 *  bugs in the software that can cause potential loss (monetarily or otherwise) *
 *  to the user.                                                                 *
 *                                                                               *
 *  Please contact Divine Cloud if you need additional information or have any   *
 *  questions.                                                                   *
 *********************************************************************************/

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
