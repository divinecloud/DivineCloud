package com.dc.runbook.rt.node;

import com.dc.node.NodeDetails;
import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.exec.SshClient;
import com.dc.ssh.client.exec.SshClientImpl;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.support.KeyValuePair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnDemandNodesProvider implements OnDemandNodesCleaner {

    private Map<String, Map<String, NodeDetails>> nodeDetailsMap;
    private Map<String, NodeCredentials> nodeCredentialsMap;

    private Map<String, SshClient> nodeClientMap;
    private SshClientConfiguration configuration;

    public OnDemandNodesProvider() {
        nodeDetailsMap = new ConcurrentHashMap<>();
        nodeClientMap = new ConcurrentHashMap<>();
        nodeCredentialsMap = new ConcurrentHashMap<>();
        configuration = new SshClientConfiguration.Builder().ptySupport(true).build();
    }

    public void addNodes(String dynamicTag, List<KeyValuePair<NodeDetails, NodeCredentials>> nodes) {
        synchronized(dynamicTag.intern()) {
            Map<String, NodeDetails> detailsMap = nodeDetailsMap.get(dynamicTag);

            if(detailsMap == null) {
                detailsMap = new ConcurrentHashMap<>();
                nodeDetailsMap.put(dynamicTag, detailsMap);
            }
            for(KeyValuePair<NodeDetails, NodeCredentials> nodeKeyValuePair : nodes) {
                detailsMap.put(nodeKeyValuePair.getKey().getUniqueId(), nodeKeyValuePair.getKey());
                nodeCredentialsMap.put(nodeKeyValuePair.getKey().getUniqueId(), nodeKeyValuePair.getValue());
            }
        }
    }

    public void addNode(String dynamicTag, NodeDetails nodeDetails, NodeCredentials nodeCredentials) {
        if(dynamicTag != null && nodeDetails != null && nodeDetails.getUniqueId() != null && nodeCredentials != null) {
            synchronized (dynamicTag.intern()) {
                Map<String, NodeDetails> detailsMap = nodeDetailsMap.get(dynamicTag);
                if (detailsMap == null) {
                    detailsMap = new ConcurrentHashMap<>();
                    nodeDetailsMap.put(dynamicTag, detailsMap);
                }
                detailsMap.put(nodeDetails.getUniqueId(), nodeDetails);
                nodeCredentialsMap.put(nodeDetails.getUniqueId(), nodeCredentials);
            }
        }
    }

    public SshClient getClient(String nodeUniqueId) {
        SshClient result;
        synchronized (nodeUniqueId.intern()) {
            result = nodeClientMap.get(nodeUniqueId);
            if(result == null) {
                NodeCredentials nodeCredentials = nodeCredentialsMap.get(nodeUniqueId);
                result = new SshClientImpl(nodeCredentials, configuration);
                nodeClientMap.put(nodeUniqueId, result);
            }
        }
        return result;
    }

    private NodeDetails findNodeDetails(String dynamicTag, String nodeUniqueId) {
        NodeDetails result = null;
        Map<String, NodeDetails> map = nodeDetailsMap.get(dynamicTag);
        if(map != null) {
            result = map.get(nodeUniqueId);
        }
        return result;
    }

    public Map<String, NodeDetails> getNodes(String dynamicTag) {
        Map<String, NodeDetails> result;
        synchronized (dynamicTag.intern()) {
            result = nodeDetailsMap.get(dynamicTag);
        }
        return result;
    }

    public void cleanup() {
        for(SshClient client : nodeClientMap.values()) {
            try {
                client.close();
            }
            catch(Throwable t) {
                t.printStackTrace(); // continue closing ssh clients even if some clients fail
            }
        }
    }
}
