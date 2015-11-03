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

package com.dc.ssh.client.support;

import com.dc.ssh.client.shell.SshUserInfo;

import java.util.Map;

public class SshClientConnectInfo implements SshUserInfo {
	private SessionBasedAuthData	          authData;
	private String	                          passcode;
	private boolean	                          sessionBased;
	private Map<String, SessionBasedAuthData>	sessionBasedAuthDataMap;
	private String	                          userCredentialId;

	public SshClientConnectInfo(SessionBasedAuthData authData, String passcode, boolean sessionBased, String userCredentialId, Map<String, SessionBasedAuthData> sessionBasedAuthDataMap) {
		this.userCredentialId = userCredentialId;
		this.sessionBasedAuthDataMap = sessionBasedAuthDataMap;
		this.passcode = passcode;
		this.authData = authData;
		this.sessionBased = sessionBased;
	}

	@Override
	public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {

		String[] answers = new String[1];
        if(prompt[0].equals("Password: ")) {
            answers[0] = authData.getPassword();
        }
        else {
            answers[0] = passcode;
        }
		return answers;
	}

	@Override
	public String getPassphrase() {
		return authData.getPassphrase();
	}

	@Override
	public String getPassword() {
		return authData.getPassword();
	}

	@Override
	public boolean promptPassword(String message) {
        return true;
	}

	@Override
	public boolean promptPassphrase(String message) {
		return true;
	}

	@Override
	public boolean promptYesNo(String message) {
		return true;
	}

	@Override
	public void showMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void updateSshConnectInfoInSession() {
        if(sessionBasedAuthDataMap != null) {
            if (sessionBased) {
                sessionBasedAuthDataMap.put(userCredentialId, authData);
            }

            if(authData != null) {
                if(authData.getPassword() == null && authData.getPassphrase() != null) {
                    sessionBasedAuthDataMap.put(CredentialsMapKeySupport.generatePassphraseCredentialId(userCredentialId), authData);
                }
            }
        }
	}
}
