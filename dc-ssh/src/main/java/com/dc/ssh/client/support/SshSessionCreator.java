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

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshConnectException;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;
import java.util.Vector;

/**
 * Single class for dealing with all the Ssh session related operations.
 */
public class SshSessionCreator {

    private JSch jsch;
    private static volatile boolean initialized;
    private static SshSessionCreator instance;

    private SshSessionCreator() {
        jsch = new JSch();
    }

    public static SshSessionCreator getInstance() {
        if(initialized) {
            return instance;
        }
        synchronized(SshSessionCreator.class) {
            if(!initialized) {
                instance = new SshSessionCreator();
                initialized = true;
            }
        }
        return instance;
    }

    public void disconnectSession(Session session, NodeCredentials nodeCredentials) throws SshConnectException {
        session.disconnect();
        try {
            jsch.removeIdentity(nodeCredentials.getUsername());
        } catch (JSchException e) {
            throw new SshConnectException("Identity cannot be cleaned up for username : " + nodeCredentials.getUsername());
        }
    }

    public Session createSession(NodeCredentials nodeCredentials, SshClientConfiguration configuration) throws SshConnectException {
        validateHost(nodeCredentials);
        Session session;
        try {
            session = instantiateSession(nodeCredentials);
            setupKeySupportIfApplicable(nodeCredentials);
            setUpPasswordSupportIfApplicable(session, nodeCredentials);
            connectSession(session, nodeCredentials, configuration);
        }
        catch(JSchException e) {
            throw new SshConnectException("Exception occurred while creating session on '" + nodeCredentials.getHost() + "' for '" + nodeCredentials.getUsername() + "'.", e);
        }

        return session;
    }

    private void validateHost(NodeCredentials nodeCredentials) throws SshConnectException {
        String host = nodeCredentials.getHost();
        if (host == null || host.trim().length() == 0) {
            throw new SshConnectException("Invalid host '" + host + "' information provided.");
        }
    }

    private Session instantiateSession(NodeCredentials nodeCredentials) throws JSchException {
        Session session = jsch.getSession(nodeCredentials.getUsername(), nodeCredentials.getHost(), nodeCredentials.getPort());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        /*
         * These are necessary to avoid problems with latent connections being
         * closed. This tells JSCH to place a 120 second SO timeout on the
         * underlying socket. When that interrupt is received, JSCH will send a
         * keep alive message. This will repeat up to a 1000 times, which should
         * be more than enough for any long operations to prevent the socket
         * from being closed.
         *
         * SSH has a TCPKeepAlive option, but JSCH doesn't seem to ever check it:
         * session.setConfig("TCPKeepAlive", "yes");
         */
        session.setServerAliveInterval(120 * 1000);
        session.setServerAliveCountMax(1000);

        config.put("TCPKeepAlive", "yes");
        session.setConfig(config);
        return session;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void setupKeySupportIfApplicable(NodeCredentials nodeCredentials) throws JSchException, SshConnectException {
        if(nodeCredentials.isKeySupport()) {
            Vector identityNames = jsch.getIdentityNames();
            if(!isIdentityAlreadyAdded(identityNames, nodeCredentials.getUsername())) {
                jsch.addIdentity(nodeCredentials.getUsername(), nodeCredentials.getPrivateKey(), null,
                        nodeCredentials.getPassPhrase() == null ? null : nodeCredentials.getPassPhrase().getBytes());
            }
            else {
                //Identity already added, so ignore
            }
        }
    }

    private boolean isIdentityAlreadyAdded(Vector identityNames, String userName) throws SshConnectException {
        boolean present = false;
        for(Object name : identityNames) {
            if(!(name instanceof String)) {
                throw new SshConnectException("Identity names type is expected to be of String object type");
            }
            if(userName.equals(name)) {
                present = true;
                break;
            }
        }
        return present;
    }

    private void setUpPasswordSupportIfApplicable(Session session, NodeCredentials nodeCredentials) {
        if(!nodeCredentials.isKeySupport()) {
            session.setPassword(nodeCredentials.getPassword().getBytes());
        }
    }

    private void connectSession(Session session, NodeCredentials nodeCredentials, SshClientConfiguration configuration) throws JSchException, SshConnectException {
        session.connect(configuration.getReadTimeout());
        if (!session.isConnected()) {
            throw new SshConnectException("Unable to establish SSH session to node '" + nodeCredentials.getHost() + " : " + nodeCredentials.getPort() + "' for '" + nodeCredentials.getUsername() + "'.");
        }
    }

}
