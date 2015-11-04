package com.dc.ssh.client.support;


public class SshCredentialInfo {
	private String	             userCredentialId;
	private SessionBasedAuthData	authData;
	private String	             passcode;

	public String getUserCredentialId() {
		return userCredentialId;
	}

	public void setUserCredentialId(String userCredentialId) {
		this.userCredentialId = userCredentialId;
	}

	public SessionBasedAuthData getAuthData() {
		return authData;
	}

	public void setAuthData(SessionBasedAuthData authData) {
		this.authData = authData;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

}
