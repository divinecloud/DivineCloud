package com.dc.ssh.client.exec.vo;

import com.dc.LinuxOSType;

/**
 * Holds all the relevant credentials for the given node.
 */
public class NodeCredentials {
    private String id;
    private String  host;
    private int     port;
    private String  username;
    private String  password;
    private byte[]  privateKey;
    private String  passPhrase;
    private boolean keySupport;
    private long lastUpdateTime; //Needed so top level APIs using this obj know if credentials have changed.
    private boolean sessionBased;
    private String passcode;
    private String userCredentialId;
    private LinuxOSType linuxOSType;
    
    public NodeCredentials() {
        //empty constructor for marshaling/un-marshaling
    }

    public static class Builder {
        private String id;
        private String  host;
        private int     port = 22; //default port is 22
        private String  username;
        private String  password;
        private byte[]  privateKey;
        private String  passPhrase;
        private boolean keySupport;
        private boolean sessionBased;
        private String passcode;
        private long lastUpdateTime;
        private String userCredentialId;
        private LinuxOSType linuxOSType = LinuxOSType.Any; //default value

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder lastUpdateTime(long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder privateKey(byte[] privateKey) {
            this.privateKey = privateKey;
            this.keySupport = true;
            return this;
        }

        public Builder passPhrase(String passPhrase) {
            this.passPhrase = passPhrase;
            return this;
        }

        public Builder keySupport(boolean support) {
            this.keySupport = support;
            return this;
        }

        public Builder sessionBased(boolean sessionBased) {
            this.sessionBased = sessionBased;
            return this;
        }

        public Builder userCredentialId(String userCredentialId) {
            this.userCredentialId = userCredentialId;
            return this;
        }

        public Builder linuxOSType(LinuxOSType type) {
            this.linuxOSType = type;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder passcode(String passcode) {
            this.passcode = passcode;
            return this;
        }
        public NodeCredentials build() {
            return new NodeCredentials(this);
        }

        public Builder(String host, String username) {
            this.host = host;
            this.username = username;
            if(this.id == null) {
                this.id = host;
            }
        }

        public Builder(String host) {
            this.host = host;
            if(this.id == null) {
                this.id = host;
            }
        }

    }

    private NodeCredentials(Builder builder) {
        this.id = builder.id;
        this.host = builder.host;
        this.username = builder.username;
        this.port = builder.port;
        this.password = builder.password;
        this.privateKey = builder.privateKey;
        this.passPhrase = builder.passPhrase;
        this.keySupport = builder.keySupport;
        this.sessionBased = builder.sessionBased;
        this.passcode = builder.passcode;
        this.lastUpdateTime = builder.lastUpdateTime;
        this.userCredentialId = builder.userCredentialId;
        this.linuxOSType = builder.linuxOSType;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public boolean isKeySupport() {
        return keySupport;
    }

    public boolean isSessionBased() {
        return sessionBased;
    }

    public String getPasscode() {
        return passcode;
    }

    public int getPort() {
        return port;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getUserCredentialId() {
    	return userCredentialId;
    }

    public LinuxOSType getLinuxOSType() {
        return linuxOSType;
    }
}
