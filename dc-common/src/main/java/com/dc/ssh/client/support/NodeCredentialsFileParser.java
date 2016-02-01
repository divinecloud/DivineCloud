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

import com.dc.DcException;
import com.dc.ssh.client.exec.vo.NodeCredentialKeys;
import com.dc.ssh.client.exec.vo.NodeCredentials;
import com.dc.support.KeyValuePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class NodeCredentialsFileParser {

    /*
     * Parses the node details text. The text is expected in the following format:
     *
     * STEP:<STEP>, ID:<NODE_DISPLAY_ID>, HOST:<HOST_NAME_OR_IP>, PORT:<PORT_NUMBER>, USERNAME:<USER_NAME>, PASSWORD:<PASSWORD>, PRIVATE_KEY:<PRIVATE_KEY>, PASS_PHRASE:<PASS_PHRASE>, JUMP_HOST:<JUMP_HOST>, AUTH_MODE:M, PASSCODE:<PASSCODE>
     *
     * @param nodeCredentialsText - node details text
     * @return list of node details object
     */
    public static List<List<NodeCredentials>> parse(String nodeCredentialsText, boolean forRunBook) throws DcException {
        Map<Integer, List<NodeCredentials>> map = new HashMap<>();
        if(nodeCredentialsText != null) {
            String[] lines = nodeCredentialsText.split("\n");
            for (String line : lines) {
                if (!"".equals(line.trim())) {
                    String[] fields = line.split(",");

                    if (fields.length > 0) {
                        Map<String, String> columnsMap = new HashMap<>();

                        for (String field : fields) {
                            int index = field.indexOf(":");
                            if (index > 0) {
                                String columnKey = field.substring(0, index);
                                String columnValue = field.substring(index + 1);
                                columnsMap.put(columnKey.trim(), columnValue.trim());
                            }
                        }

                        KeyValuePair<Integer, NodeCredentials> nodeCredentialsPair = convert(columnsMap, forRunBook);
                        List<NodeCredentials> nodeCredEntry = map.get(nodeCredentialsPair.getKey());
                        if(nodeCredEntry == null) {
                            nodeCredEntry = new ArrayList<>();
                            map.put(nodeCredentialsPair.getKey(), nodeCredEntry);
                        }
                        nodeCredEntry.add(nodeCredentialsPair.getValue());
                    }
                }
            }
        }

        if(map.size() == 0) {
            throw new DcException("Invalid Node Details provided in Node Credentials file");
        }
        return convertToList(map);
    }

    private static List<List<NodeCredentials>> convertToList(Map<Integer, List<NodeCredentials>> map) {
        List<List<NodeCredentials>> result = new ArrayList<>();
        Set<Integer> keySet = map.keySet();

        SortedSet<Integer> sortedSet = new TreeSet<>(keySet);
        List<Integer> emptyStepKeys = new ArrayList<>();
        Iterator<Integer> iterator = sortedSet.iterator();
        int startPoint = 1;
        while(iterator.hasNext()) {
            Integer current = iterator.next();
            if(current == startPoint) {
                startPoint++;
            }
            else {
                while(startPoint < current) {
                    emptyStepKeys.add(startPoint++);
                }
                startPoint++;
            }
        }

        for(Integer key : emptyStepKeys) {
            map.put(key, new ArrayList<>());
        }

        Set<Integer> mapKeys = map.keySet();
        SortedSet<Integer> sortedMapKeys = new TreeSet<>(mapKeys);
        Iterator<Integer> mapIterator = sortedMapKeys.iterator();
        while(mapIterator.hasNext()) {
            Integer key = mapIterator.next();
            List<NodeCredentials> nodeCred = map.get(key);
            result.add(nodeCred);
        }

        //result.addAll(sortedSet.stream().map(map::get).collect(Collectors.toList()));
        return result;
    }


    private static KeyValuePair<Integer,NodeCredentials> convert(Map<String, String> columnsMap, boolean forRunBook) {
        KeyValuePair<Integer, NodeCredentials> pair = new KeyValuePair<>();
        int stepNumber = Integer.parseInt(columnsMap.get(NodeCredentialKeys.STEP.name()));
        String host = columnsMap.get(NodeCredentialKeys.HOST.name());
        String username = columnsMap.get(NodeCredentialKeys.USERNAME.name());
        if(forRunBook && (host == null || username == null || stepNumber < 1)) {
            throw new DcException("Invalid Node Credentials record provided in the file : " + host + " " + username);
        }
        else {
            stepNumber = 1;
        }
        NodeCredentials.Builder resultBuilder = new NodeCredentials.Builder(host, username);

        // PASS_PHRASE:<PASS_PHRASE>
        if(columnsMap.get(NodeCredentialKeys.ID.name()) != null) {
            resultBuilder.id(columnsMap.get(NodeCredentialKeys.ID.name()));
        } else {
            resultBuilder.id(host);
        }

        if(columnsMap.get(NodeCredentialKeys.PORT.name()) != null) {
            resultBuilder.port(Integer.parseInt(columnsMap.get(NodeCredentialKeys.PORT.name())));
        }
        else {
            resultBuilder.port(22);
        }

        if(columnsMap.get(NodeCredentialKeys.PASSWORD.name()) != null) {
            resultBuilder.password(columnsMap.get(NodeCredentialKeys.PASSWORD.name()));
        }

        if(columnsMap.get(NodeCredentialKeys.PRIVATE_KEY.name()) != null) {
            String privateKeyFile = columnsMap.get(NodeCredentialKeys.PRIVATE_KEY.name());
            byte[] privateKeyBytes;
            try {
                privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyFile));
            } catch (IOException e) {
                throw new DcException("Cannot read the private key file : " + privateKeyFile, e);
            }
            resultBuilder.privateKey(privateKeyBytes);
        }
        if(columnsMap.get(NodeCredentialKeys.PASS_PHRASE.name()) != null) {
            resultBuilder.passPhrase(columnsMap.get(NodeCredentialKeys.PASS_PHRASE.name()));
        }

        pair.setKey(stepNumber);
        pair.setValue(resultBuilder.build());
        return pair;
    }
}
