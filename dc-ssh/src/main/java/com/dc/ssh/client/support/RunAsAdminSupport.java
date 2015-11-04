package com.dc.ssh.client.support;

public class RunAsAdminSupport {

    public static String conditionallyAddSuperUser(String commandString, String runAs, boolean admin) {
        String updatedCommand = commandString;
        if (runAs != null && !runAs.trim().isEmpty()) {
            updatedCommand = addRunAsUserName(commandString, runAs);
        } else {
            if (admin) {
                updatedCommand = addRunAsAdmin(commandString);
            }//else do nothing
        }
        return updatedCommand;
    }

    private static String addRunAsAdmin(String commandString) {
        String updatedCommand = "sudo " + commandString;
        return updatedCommand;
    }

    private static String addRunAsUserName(String commandString, String runAs) {
        String escapedCommandString = escapeSingleQuotes(commandString);
        return "sudo su -c '" + escapedCommandString + "' " + runAs;
    }

    private static String escapeSingleQuotes(String commandString) {
        return commandString.replaceAll("'", "'\\\\''");
    }

}
