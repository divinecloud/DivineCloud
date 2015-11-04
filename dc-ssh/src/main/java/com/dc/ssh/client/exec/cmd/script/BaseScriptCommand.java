package com.dc.ssh.client.exec.cmd.script;


import com.dc.ssh.client.exec.cmd.AbstractSshCommand;

/**
 * Marker class for all the script commands.
 */
public abstract class BaseScriptCommand extends AbstractSshCommand {
    private ScriptAttributes scriptAttributes;

    public BaseScriptCommand(ScriptAttributes scriptAttributes) {
        super(scriptAttributes.getCommandAttributes());
        this.scriptAttributes = scriptAttributes;
    }

    public abstract String getCode();

    public String getArguments() {
        return scriptAttributes.getArguments();
    }

    public ScriptLanguage getLanguage() {
        return scriptAttributes.getLanguage();
    }

    public String getInvokingProgram() {
        return scriptAttributes.getInvokingProgram();
    }

    public String getFilePath() {
        return scriptAttributes.getFilePath();
    }

    @Override
    public String toString() {
        return "ScriptCommand{" +
                "scriptAttributes=" + scriptAttributes +
                '}';
    }
}
