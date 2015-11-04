package com.dc.ssh.client.exec.vo;


public class Credential {
    private String name;
    private String userName;
    private String jumpHost;
    private String password;
    private String privateKey;
    private String passPhrase;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJumpHost() {
        return jumpHost;
    }

    public void setJumpHost(String jumpHost) {
        this.jumpHost = jumpHost;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }
}
