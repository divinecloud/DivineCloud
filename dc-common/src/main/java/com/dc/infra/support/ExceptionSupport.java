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
