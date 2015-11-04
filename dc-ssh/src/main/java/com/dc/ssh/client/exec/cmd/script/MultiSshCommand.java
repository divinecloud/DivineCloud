package com.dc.ssh.client.exec.cmd.script;

import com.dc.ssh.client.exec.cmd.SshCommandAttributes;

import java.util.List;

/**
 * Multiple commands input implementation for SshCommand.
 */
public class MultiSshCommand extends BaseScriptCommand {
    private List<String> commands;

    public MultiSshCommand(String executionId, List<String> commands) {
        super(new ScriptAttributes(new SshCommandAttributes(executionId), null, ScriptLanguage.Shell, "bash", null));
        this.commands = commands;
    }

    public MultiSshCommand(ScriptAttributes scriptAttributes, List<String> commands) {
        super(scriptAttributes);
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }


    public String getCode() {

        List<String> cmdStrings = commands;
        StringBuilder resultBuilder = new StringBuilder();
        for(String cmd : cmdStrings) {
            resultBuilder.append(cmd).append('\n');
        }
        return resultBuilder.toString();
    }

    @Override
    public String toString() {
        return "MultiSshCommand{" +
                "commands=" + commands +
                "} " + super.toString();
    }

    @Override
    public String prettyCode() {
        String code = "";
        for(String str : commands) {
            code += str + '\n';
        }
        String result = code;
        if(code.length() > 64) {
            result = code.substring(0, 64) + " ...";
        }
        return result;
    }

}
