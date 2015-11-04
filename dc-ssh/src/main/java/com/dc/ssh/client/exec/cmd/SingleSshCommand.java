package com.dc.ssh.client.exec.cmd;

/**
 * Single command implementation for SshCommand.
 */
public class SingleSshCommand extends AbstractSshCommand {
    private String command;


    public SingleSshCommand(SshCommandAttributes cmdAttributes, String command) {
        super(cmdAttributes);
        this.command = command;
    }

    public SingleSshCommand(String executionId, String command) {
        super(new SshCommandAttributes(executionId, null, null, false));
        this.command = command;
    }


    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "SingleSshCommand{" +
                "command='" + command + '\'' +
                "} " + super.toString();
    }

    @Override
    public String prettyCode() {
        String result = command;

        if(command != null && command.length() > 64) {
            result = command.substring(0, 64) + " ...";
        }
        return result;
    }
}
