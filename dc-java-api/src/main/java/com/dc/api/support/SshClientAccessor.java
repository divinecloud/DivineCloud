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

package com.dc.api.support;

import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.SshClientImpl;
import com.dc.ssh.client.exec.vo.NodeCredentials;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SshClientAccessor {

    private Map<String, SshClient> sshClientCache;
    private SshClientConfiguration clientConfiguration;
    private static String SSH_CLIENT_ACCESSOR = "SSH_CLIENT_ACCESSOR";

    public SshClientAccessor() {
        clientConfiguration = new SshClientConfiguration.Builder().ptySupport(true).build();
        sshClientCache = new ConcurrentHashMap<>();
    }

    public boolean exists(String nodeId) {
        return sshClientAvailable(nodeId);
    }

    public boolean isConnected(String nodeId) {
        boolean connected = false;
        if (sshClientCache.containsKey(nodeId)) {
            SshClient sshClient = sshClientCache.get(nodeId);
            if (sshClient != null) {
                connected = sshClient.isConnected();
            }
        }
        return connected;
    }

    public boolean isIdle(String nodeId) {
        boolean idle = false;
        if (sshClientCache.containsKey(nodeId)) {
            SshClient sshClient = sshClientCache.get(nodeId);
            if (sshClient != null) {
                idle = sshClient.isIdle();
            }
        }
        return idle;

    }

    public SshClient provide(NodeCredentials nodeCredentials) {
        if (!sshClientAvailable(nodeCredentials.getId(), false)) {
            synchronized ((SSH_CLIENT_ACCESSOR + nodeCredentials.getId()).intern()) {
                if(!sshClientAvailable(nodeCredentials.getId())) {
                    SshClient sshClient = new SshClientImpl(nodeCredentials, clientConfiguration);
                    sshClientCache.put(nodeCredentials.getId(), sshClient);
                }
            }
        }
        return sshClientCache.get(nodeCredentials.getId());
    }

    public SshClient get(String nodeId) {
        SshClient result;
        result = sshClientCache.get(nodeId);
        if(result == null) {
            synchronized ((SSH_CLIENT_ACCESSOR + nodeId).intern()) {
                result = sshClientCache.get(nodeId);
            }
        }
        return result;
    }

    private boolean sshClientAvailable(String nodeId) {
        return sshClientAvailable(nodeId, true);
    }

    private boolean sshClientAvailable(String nodeId, boolean ping) {
        boolean available = false;
        if (sshClientCache.containsKey(nodeId)) {
            SshClient sshClient = sshClientCache.get(nodeId);
            if (sshClient != null) {
                if(sshClient.isConnected()) {
                    if(ping || sshClient.isIdle()) {
                        try {
                            ExecutionDetails details = sshClient.execute("echo CONNECT_CHECK");

                            String output = new String(details.getOutput());

                            if (output.contains("CONNECT_CHECK")) {
                                available = true;
                            }
                        } catch (Exception e) {
                            System.out.println("Unable to execute connect check on node id " + nodeId);
                            e.printStackTrace();
                        }
                        if (!available) {
                            sshClientCache.remove(nodeId);
                        }
                    }
                    else {
                        available = true;
                    }
                }
            }
        }

        return available;
    }

    public boolean ping(String nodeId) {
        return ping(nodeId, 4000);
    }

    public boolean ping(String nodeId, long timeoutThreshold) {
        boolean available = false;
        if (sshClientCache.containsKey(nodeId)) {
            SshClient sshClient = sshClientCache.get(nodeId);
            if (sshClient != null) {
                if(sshClient.isConnected()) {
                    try {
                        ExecutionDetails details = sshClient.execute("echo CONNECT_CHECK", timeoutThreshold);

                        String output = new String(details.getOutput());

                        if (output.contains("CONNECT_CHECK")) {
                            available = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Unable to execute connect check on node id " + nodeId);
                        e.printStackTrace();
                    }
                    if (!available) {
                        sshClientCache.remove(nodeId);
                    }
                }
            }
        }
        return available;
    }

}
