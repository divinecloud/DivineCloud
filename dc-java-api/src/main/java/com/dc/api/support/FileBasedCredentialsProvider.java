package com.dc.api.support;

import com.dc.DcException;
import com.dc.node.NodeDetails;
import com.dc.runbook.rt.CredentialsProvider;
import com.dc.ssh.client.exec.vo.Credential;
import com.dc.ssh.client.exec.vo.NodeCredentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class FileBasedCredentialsProvider implements CredentialsProvider {
    private Map<String, Credential> credentialMap;

    public FileBasedCredentialsProvider(Map<String, Credential> credentialMap) {
        this.credentialMap = credentialMap;
    }

    @Override
    public NodeCredentials provide(NodeDetails nodeDetails) {
        NodeCredentials result = null;
        Credential credential = credentialMap.get(nodeDetails.getCredentialName());
        if(credential != null) {
            NodeCredentials.Builder resultBuilder = new NodeCredentials.Builder(nodeDetails.getHost(), credential.getUserName());
            resultBuilder.password(credential.getPassword());
            if(credential.getPrivateKey() != null) {
                resultBuilder.keySupport(true);
                resultBuilder.passPhrase(credential.getPassPhrase());
                try {
                    byte[] privateKeyBytes = Files.readAllBytes(Paths.get(credential.getPrivateKey()));
                    resultBuilder.privateKey(privateKeyBytes);
                } catch (IOException e) {
                    throw new DcException("Unable to read private key file : " + credential.getPrivateKey());
                }
            }
            resultBuilder.id(nodeDetails.getUniqueId());
            result = resultBuilder.build();
        }
        return result;
    }
}
