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

package com.dc.ssh.client.support;

import com.dc.ssh.client.shell.SshUserInfo;
import com.dc.ssh.client.exec.vo.NodeCredentials;

import java.util.Map;

public class SshShellConnectInfo implements SshUserInfo {
	private NodeCredentials	                  nodeCredentials;
	private CredentialAccessor	              credentialAccessor;
	private Map<String, SessionBasedAuthData>	sessionBasedAuthDataMap;
	private SessionBasedAuthData	          authData = new SessionBasedAuthData();

	public SshShellConnectInfo(NodeCredentials nodeCredentials) {
		this.nodeCredentials = nodeCredentials;
	}

	public SshShellConnectInfo(NodeCredentials nodeCredentials, CredentialAccessor credentialAccessor, Map<String, SessionBasedAuthData> sessionBasedAuthDataMap) {
		this.sessionBasedAuthDataMap = sessionBasedAuthDataMap;
		this.nodeCredentials = nodeCredentials;
        if(nodeCredentials != null && !nodeCredentials.isKeySupport() && !nodeCredentials.isSessionBased() && nodeCredentials.getPassword() != null) {
            authData.setPassword(nodeCredentials.getPassword());
            authData.setPwd(true);
        }
		this.credentialAccessor = credentialAccessor;
	}

	@Override
	public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
		String[] answers = new String[1];
		String message = prompt[0];
		answers[0] = credentialAccessor.access(message);
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
		String password = nodeCredentials.getPassword();
		authData.setPwd(true);
		if (password == null) {
			if (sessionBasedAuthDataMap.containsKey(nodeCredentials.getUserCredentialId())) {
				password = sessionBasedAuthDataMap.get(nodeCredentials.getUserCredentialId()).getPassword();
			}
			if (password == null) {
				password = credentialAccessor.access(message);
			}
		}
		authData.setPassword(password);
		return true;
	}

	@Override
	public boolean promptPassphrase(String message) {
		String passphrase = nodeCredentials.getPassPhrase();
		authData.setPwd(false);
		if (passphrase == null) {
			if (sessionBasedAuthDataMap.containsKey(nodeCredentials.getUserCredentialId())) {
				passphrase = sessionBasedAuthDataMap.get(nodeCredentials.getUserCredentialId()).getPassphrase();
			}
			if (passphrase == null) {
				passphrase = credentialAccessor.access(message);
			}
		}
		authData.setPassphrase(passphrase);
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
		if(nodeCredentials.isSessionBased() && sessionBasedAuthDataMap != null) {
			sessionBasedAuthDataMap.put(nodeCredentials.getUserCredentialId(), authData);
		}
	}
}
