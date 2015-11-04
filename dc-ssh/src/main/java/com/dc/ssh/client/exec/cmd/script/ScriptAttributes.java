package com.dc.ssh.client.exec.cmd.script;

import com.dc.ssh.client.exec.cmd.SshCommandAttributes;

public class ScriptAttributes {

    private SshCommandAttributes commandAttributes;
    private String arguments;
    private ScriptLanguage language;
    private String invokingProgram;
    private String filePath;

    public ScriptAttributes(SshCommandAttributes commandAttributes, String arguments, ScriptLanguage language, String invokingProgram, String filePath) {
        this.commandAttributes = commandAttributes;
        this.arguments = arguments;
        this.language = language;
        this.invokingProgram = invokingProgram;
        this.filePath = filePath;
    }

    public SshCommandAttributes getCommandAttributes() {
        return commandAttributes;
    }

    public String getArguments() {
        return arguments;
    }

    public ScriptLanguage getLanguage() {
        return language;
    }

    public String getInvokingProgram() {
        return invokingProgram;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return "ScriptAttributes{" +
                "commandAttributes=" + commandAttributes +
                ", arguments='" + arguments + '\'' +
                ", language=" + language +
                ", invokingProgram='" + invokingProgram + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
