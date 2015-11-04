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
