package com.dc.ssh.client.support;

public class SessionBasedAuthData {
	private String	password;
	private String	privateKey;
	private String	passphrase;

	private boolean	pwd;
	private boolean credPresent;

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

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public boolean isPwd() {
		return pwd;
	}

	public void setPwd(boolean pwd) {
		this.pwd = pwd;
	}

	public boolean isCredPresent() {
		return credPresent;
	}

	public void setCredPresent(boolean credPresent) {
		this.credPresent = credPresent;
	}

}
