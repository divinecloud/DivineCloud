package com.dc.ssh.client.support;

import java.util.HashMap;
import java.util.Map;

import com.dc.ssh.client.exec.vo.ShellType;

/**
 * Shebang values for different shell types.
 */
public class Shebang {
    private static Map<ShellType, String> shebangMap;

    static {
        shebangMap = new HashMap<>();
        shebangMap.put(ShellType.BASH, "#!/bin/bash" + '\n');
        shebangMap.put(ShellType.SH, "#!/bin/sh" + '\n');
        shebangMap.put(ShellType.TCSH, "#!/bin/tcsh -f" + '\n');
    }

    public static String shebangFor(ShellType shellType) {
        return shebangMap.get(shellType);
    }
}
