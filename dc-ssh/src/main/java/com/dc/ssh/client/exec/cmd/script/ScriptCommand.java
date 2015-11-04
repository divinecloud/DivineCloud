package com.dc.ssh.client.exec.cmd.script;


import com.dc.ssh.client.exec.cmd.SshCommandAttributes;

public class ScriptCommand extends BaseScriptCommand {
    private String code;

    public ScriptCommand(String executionId, String code, ScriptLanguage language, String invokingProgram) {
        super(new ScriptAttributes(new SshCommandAttributes(executionId), null, language, invokingProgram, null));
        this.code = code;
    }

    public ScriptCommand(ScriptAttributes scriptAttributes, String code) {
        super(scriptAttributes);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String prettyCode() {
        String result = code;
        if(code != null && code.length() > 64) {
            result = code.substring(0, 64) + " ...";
        }
        return result;
    }
}
