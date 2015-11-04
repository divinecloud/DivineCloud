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
