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

package com.dc.api.domain;

import java.io.File;

public class Credential {
    private String id;
    private CredentialType type;
    private String osUserName;
    private String password;
    private String passphrase;
    private File keyPath;

    public Credential(String id, String osUserName, String password) {
        this.osUserName = osUserName;
        this.password = password;
        this.id = id;
        type = CredentialType.PASSWORD;
    }

    public Credential(String id, String osUserName, File keyPath, String passphrase) {
        this.osUserName = osUserName;
        this.id = id;
        this.passphrase = passphrase;
        this.keyPath = keyPath;
        type = CredentialType.PRIVATE_KEY;
    }

    public String getId() {
        return id;
    }

    public String getOsUserName() {
        return osUserName;
    }

    public CredentialType getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public File getKeyPath() {
        return keyPath;
    }
}
