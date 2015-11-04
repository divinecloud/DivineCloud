package com.dc.ssh.client.script;

import com.dc.ssh.client.exec.vo.ShellType;

public interface ShellScriptBuilder {

    public ShellScriptBuilder shell(ShellType shellType);

    public ShellScriptBuilder mkdir();

    public ShellScriptBuilder cd();

    public ShellScriptBuilder mv();

    public ShellScriptBuilder rm();

    public ShellScriptBuilder set();

    public ShellScriptBuilder transfer();

    public ShellScriptBuilder create();

    public ShellScriptBuilder append(String scriptCode);

    public ShellScriptBuilder kill(String processInfo);

    public ShellScriptBuilder zip();

    public ShellScriptBuilder unzip();

    public ShellScriptBuilder tar();

    public ShellScriptBuilder untar();

    public ShellScript build();
}
