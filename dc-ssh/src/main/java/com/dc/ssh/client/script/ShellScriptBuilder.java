/*
 * Copyright (C) 2014 Divine Cloud Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
