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

package com.dc.ssh.client.shell;

import com.dc.ssh.client.SshConnectException;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.ssh.client.support.SshExceptionParser;
import com.dc.support.KeyValuePair;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AbstractSshClient {
    protected List<NodeConfig> nodeConfigList;
    protected Session[]	           sessions;
    protected Session currentSession;
    protected SshClientConfiguration configuration;
    private JSch jsch;

	public boolean isConnected() {
        return currentSession != null && currentSession.isConnected();
    }

    public AbstractSshClient(List<NodeConfig> nodeConfigList, SshClientConfiguration configuration) {
        this.configuration = configuration;
        initialize(nodeConfigList);
    }

    public AbstractSshClient(NodeConfig nodeConfig, SshClientConfiguration configuration) {
        this.configuration = configuration;
        List<NodeConfig> list = new ArrayList<>(1);
        list.add(nodeConfig);
        initialize(list);
    }

    public String id() {
        NodeConfig nodeConfig = nodeConfigList.get(nodeConfigList.size() - 1);
        return nodeConfig.getNodeCredentials().getId();
    }

    public String ip() {
        return currentSession.getHost();
    }

    private void initialize(List<NodeConfig> nodeConfigList) {
        this.nodeConfigList = nodeConfigList;
        sessions = new Session[nodeConfigList.size()];
        jsch = new JSch();
        createSession();
    }

    protected void createSession() {
        String host = null;
        try {
            Session session;
            NodeConfig nodeConfig = nodeConfigList.get(0);
            NodeCredentials credential1 = nodeConfig.getNodeCredentials();
            host = credential1.getHost();
            String user = credential1.getUsername();
            addIdentity(nodeConfig.getNodeCredentials());
            if (credential1.isKeySupport()) {
                session = getSessionForKey(credential1);
            } else {
                session = getSessionForPwd(host, user);
            }

            session.connect(configuration.getConnectWaitTime());
            nodeConfig.getSshUserInfo().updateSshConnectInfoInSession();

            for (int i = 1; i < nodeConfigList.size(); i++) {
                nodeConfig = nodeConfigList.get(i);
                addIdentity(nodeConfig.getNodeCredentials());
                host = nodeConfig.getNodeCredentials().getHost();
                user = nodeConfig.getNodeCredentials().getUsername();

                int assignedPort = session.setPortForwardingL(0, host, nodeConfig.getNodeCredentials().getPort());
                sessions[i] = session = jsch.getSession(user, "127.0.0.1", assignedPort);

                session.setUserInfo(nodeConfig.getSshUserInfo());
                session.setHostKeyAlias(host);
                session.connect(configuration.getConnectWaitTime());
                nodeConfig.getSshUserInfo().updateSshConnectInfoInSession();
            }

            currentSession = session;
        } catch (JSchException | NullPointerException e) {
            KeyValuePair<String, String> pair = SshExceptionParser.failedConnectionCause(e, host);
            throw new SshConnectException(pair.getValue(), e, true, pair.getKey());
        } catch (Throwable e) {
            e.printStackTrace();
            KeyValuePair<String, String> pair = SshExceptionParser.failedConnectionCause(e, host);
            throw new SshConnectException(pair.getValue(), e, true, pair.getKey());
        }
    }

    private void addIdentity(NodeCredentials credential) throws JSchException {
        if (credential.isKeySupport()) {
            String key = new String(DigestUtils.md5(credential.getPrivateKey()));
            if(!jsch.getIdentityNames().contains(key)) {
                jsch.addIdentity(key, credential.getPrivateKey(), null, null);
            }
        }
    }

    private Session getSessionForPwd(String host, String user) throws JSchException {
        Session session;
        sessions[0] = session = jsch.getSession(user, host, nodeConfigList.get(0).getNodeCredentials().getPort());
        Properties config = new Properties();
        config.put("PubKeyAuthentication", "no");
        config.put("TCPKeepAlive", "yes");
        session.setConfig(config);
        session.setUserInfo(nodeConfigList.get(0).getSshUserInfo());
        return session;
    }

    private Session getSessionForKey(NodeCredentials credential1) throws JSchException {
        Session session;
        sessions[0] = session = jsch.getSession(credential1.getUsername(), credential1.getHost(), credential1.getPort());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("TCPKeepAlive", "yes");
        session.setConfig(config);
        session.setUserInfo(nodeConfigList.get(0).getSshUserInfo());
        return session;
    }

    public void close() {
        for (Session session : sessions) {
            if(session != null) {
                session.disconnect();
            }
        }
    }
}
