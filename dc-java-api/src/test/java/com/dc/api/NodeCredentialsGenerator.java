package com.dc.api;

import com.dc.ssh.client.exec.vo.NodeCredentials;

public class NodeCredentialsGenerator {

    public static NodeCredentials generateServer1Credentials() {
        String host = TestSupport.getProperty("server1.host");
        String username = TestSupport.getProperty("server1.username");
        String password = TestSupport.getProperty("server1.password");
        return new NodeCredentials.Builder(host, username).password(password).id(host).build();
    }

    public static NodeCredentials generateServer2Credentials() {
        String host = TestSupport.getProperty("server2.host");
        String username = TestSupport.getProperty("server2.username");
        String keyText = TestSupport.getProperty("server2.key");
        return new NodeCredentials.Builder(host, username).id(host).privateKey(keyText.getBytes()).build();
    }

}
