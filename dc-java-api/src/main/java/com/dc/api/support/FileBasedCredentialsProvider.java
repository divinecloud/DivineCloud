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
