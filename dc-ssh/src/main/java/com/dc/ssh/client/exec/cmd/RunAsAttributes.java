package com.dc.ssh.client.exec.cmd;

public class RunAsAttributes {

    private String runAs;
    private String password;
    private boolean admin;

    public RunAsAttributes(String runAs, String password, boolean admin) {
        this.runAs = runAs;
        this.password = password;
        this.admin = admin;
    }

    public String getRunAs() {
        return runAs;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        return "RunAsAttributes{" +
                "runAs='" + runAs + '\'' +
                ", password='" + "**********" + '\'' +
                ", admin=" + admin +
                '}';
    }
}
