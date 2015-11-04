package com.dc.ssh.client.exec.support;

import com.dc.ssh.client.SshException;
import com.dc.ssh.client.exec.cmd.SingleSshCommand;
import com.dc.ssh.client.exec.cmd.script.BaseScriptCommand;
import com.dc.ssh.client.exec.cmd.script.ScriptLanguage;
import com.dc.ssh.client.support.RunAsAdminSupport;

public class CommandFormatter {

    public static String generateDefaultFileName(ScriptLanguage language, String executionId) {
        String extension;
        switch(language) {
            case Shell:
                extension = ".sh";
                break;
            case Ruby:
                extension = ".rb";
                break;
            case Perl:
                extension = ".pl";
                break;
            case Python:
                extension = ".py";
                break;
            case Groovy:
                extension = ".groovy";
                break;
            default:
                extension = ".txt";

        }
        return executionId + extension;
    }


    public static String formatCommand(SingleSshCommand command) {
        return RunAsAdminSupport.conditionallyAddSuperUser(command.getCommand(), command.runAsUser(), command.runAsAdmin());
    }

    public static String formatCommand(BaseScriptCommand command, String destination) throws SshException {
        String commandString;
        String program = command.getInvokingProgram();
        if("bash".equals(program)) {
            program = "bash -e";
        }
        if(command.getArguments() != null) {
            commandString = program + " " + destination + " " + command.getArguments();
        }
        else {
            commandString = program + " " + destination;
        }

        return RunAsAdminSupport.conditionallyAddSuperUser(commandString, command.runAsUser(), command.runAsAdmin());
    }
}
