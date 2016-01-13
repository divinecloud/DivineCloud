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

package com.dc.node;

import com.dc.CloudType;

import java.util.ArrayList;
import java.util.List;

public class NodeDetailsCSVFormatParser {

    /*
     * Parses the node details text. The text is expected in the following format:
     *
     * [ID:<NODE_DISPLAY_ID>], HOST or IP[:PORT], NAME, CREDENTIALS_NAME, JUMP_HOST, STACK_NAME, [TAG1 TAG2 TAG3 ...], GROUP_PATH, M
     * The [] indicate optional values.
     *
     * @param nodeDetailsText
     *            - node details text
     * @return list of node details object
     */
    public List<NodeDetails> parse(String nodeDetailsText) {
        List<NodeDetails> nodesList = new ArrayList<>();
        String[] lines = nodeDetailsText.split("\n");
        for (String line : lines) {
            if (!"".equals(line.trim())) {
                String[] fields = line.split(",");
                int currentIndex = 0;
                if (fields.length > 0) {
                    NodeDetails nodeDetails = new NodeDetails();
                    nodeDetails.setCloudType(CloudType.Private);
                    String field1 = fields[currentIndex];
                    String host = field1;
                    if(field1 != null) {
                        field1 = field1.trim();
                        if(field1.startsWith("ID:")) {
                            setId(nodeDetails, field1);
                            currentIndex++;
                            host = fields[currentIndex];
                        }
                        setHost(nodeDetails, host.trim());
                    }
                    if (fields.length > currentIndex + 1) {
                        currentIndex++;
                        if (!"".equals(fields[currentIndex].trim())) {
                            nodeDetails.setName(fields[currentIndex].trim());
                            if (nodeDetails.getName() == null || "".equals(nodeDetails.getName())) {
                                nodeDetails.setName(nodeDetails.getHost());
                            }
                        }
                        if (fields.length > currentIndex + 1) {
                            currentIndex++;
                            if (!"".equals(fields[currentIndex].trim())) {
                                nodeDetails.setCredentialName(fields[currentIndex].trim());
                            }
                        }
                        if (fields.length > currentIndex + 1) {
                            currentIndex++;
                            if (!"".equals(fields[currentIndex].trim())) {
                                setTags(nodeDetails, fields[currentIndex]);
                                //nodeDetails.setJumpHost(fields[currentIndex].trim());
                            }
                            if (fields.length > currentIndex + 1) {
                                currentIndex++;
                                setGroupPath(nodeDetails, fields[currentIndex]);
                                if (fields.length > currentIndex + 1) {
                                    currentIndex++;
                                    if (!"".equals(fields[currentIndex].trim())) {
                                        nodeDetails.setJumpHost(fields[currentIndex].trim());
                                    }
                                    if (fields.length > currentIndex + 1) {
                                        currentIndex++;
                                        if (fields[currentIndex] != null && "M".equalsIgnoreCase(fields[currentIndex].trim())) {
                                            nodeDetails.setMultiFactorAuthenticate(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    nodesList.add(nodeDetails);
                }
            }
        }
        return nodesList;
    }

    private void setId(NodeDetails nodeDetails, String field1) {
        String id = field1.substring(field1.indexOf("ID:") + 3);
        nodeDetails.setDisplayId(id.trim());
    }

    private void setGroupPath(NodeDetails nodeDetails, String field) {
        if (field != null || !"".equals(field.trim())) {
            String groups[] = field.split("/");
            if (groups.length > 0) {
                NodeGroupInfoList groupList = new NodeGroupInfoList();
                for (String group : groups) {
                    groupList.add(new NodeGroupInfo(group, group));
                }
                nodeDetails.setGroupPaths(groupList);
            }
        }
    }

    private void setTags(NodeDetails nodeDetails, String tagNames) {
        String names[] = tagNames.split(" ");
        List<String> tagsList = new ArrayList<>();
        for (String name : names) {
            if (!"".equals(name.trim())) {
                tagsList.add(name.trim());
            }
        }
        nodeDetails.setTagNames(tagsList);
    }

    private void setHost(NodeDetails nodeDetails, String hostUrl) {
        String[] parts = hostUrl.split(":");
        if (!"".equals(parts[0].trim())) {
            nodeDetails.setHost(parts[0].trim());
        }
        if (parts.length == 2) {
            if (!"".equals(parts[1].trim())) {
                nodeDetails.setPort(Integer.parseInt(parts[1].trim()));
            }
        }

        if (nodeDetails.getPort() == 0) {
            nodeDetails.setPort(22);
        }
    }

    public static void main(String[] args) {
        NodeDetailsCSVFormatParser parser = new NodeDetailsCSVFormatParser();
        parser.parse("192.1.1.1:235, host.5.sample, cred_1, T1 T2 T_3, host2");
        //parser.parse("HOST1:80, SAMPLE Host \n" + "HOST, SAMPLE Host 2, , HOST1\n" + "Host3.a.com:22\n" + "192.168.1.1, host 3, cred_1, host1\n" + "   \n " + "\n192.1.1.1:235, host.5.sample, cred_1, host2, T1 T2 T_3");
    }
}
