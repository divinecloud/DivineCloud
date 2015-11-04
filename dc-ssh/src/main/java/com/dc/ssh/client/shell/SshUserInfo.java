package com.dc.ssh.client.shell;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public interface SshUserInfo extends UserInfo, UIKeyboardInteractive {

	void updateSshConnectInfoInSession();

}
