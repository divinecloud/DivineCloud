package com.dc.node;

import com.dc.CloudType;
import com.dc.DcException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeDetailsParser {

	/**
	 * Parses the node details text. The text is expected in the following format:
	 *
	 * [ID:<NODE_DISPLAY_ID>], HOST:<HOST_NAME_OR_IP>, [PORT:<PORT_NUMBER>], NAME:<NODE_NAME>, [CREDENTIALS_NAME:<CREDENTIALS_NAME>], [JUMP_HOST:<JUMP_HOST>], UNIQUE_ID:<UNIQUE_ID>
     *      [STACK_NAME:<STACK_NAME>], [TAGS:<TAG1 TAG2 TAG3 ...>], [GROUP_PATH:<GROUP_PATH>], [AUTH_MODE:M], ACCOUNT_NAME:<ACCOUNT_NAME>, CLOUD_TYPE:<CLOUD_TYPE>, [DYNAMIC_TAG:<DTAG1 DTAG2 ...>]
	 * The [] indicate optional values.
     *
	 * @param nodeDetailsText
	 *            - node details text
	 * @return list of node details object
	 */
	public static List<NodeDetails> parse(String nodeDetailsText) throws DcException {
		List<NodeDetails> nodesList = new ArrayList<>();
        if(nodeDetailsText != null) {
            String[] lines = nodeDetailsText.split("\n");
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

                        NodeDetails nodeDetails = convert(columnsMap);
                        if (validate(nodeDetails)) {
                            nodesList.add(nodeDetails);
                        } else {
                            int index = line.indexOf(NodeDetailsKeys.CREDENTIALS_NAME.name());
                            if (index > 0) {
                                line = line.substring(0, index);
                            }
                            System.out.println("Invalid Node Details provided for Transient Node : " + line);
                            throw new DcException("Invalid Node Details provided for Transient Node : " + line);
                        }
                    }
                }
            }
        }

        if(nodesList.size() == 0) {
            throw new DcException("Invalid Node Details provided in Transient Nodes import file");
        }
		return nodesList;
	}


    private static boolean validate(NodeDetails nodeDetails) {
        boolean result = false;
        if(nodeDetails != null) {
            if(nodeDetails.getUniqueId() != null && nodeDetails.getUniqueId().trim().length() > 0
                    && nodeDetails.getHost() != null && nodeDetails.getHost().trim().length() > 0
                    && nodeDetails.getDynamicTags() != null && nodeDetails.getDynamicTags().size() > 0
                    && nodeDetails.getCredentialName() != null && nodeDetails.getCredentialName().trim().length() > 0) {
                result = true;
            }
        }
        return result;
    }

    private static NodeDetails convert(Map<String, String> columnsMap) {
        NodeDetails result = new NodeDetails();

        if(columnsMap.get(NodeDetailsKeys.ID.name()) != null) {
            result.setDisplayId(columnsMap.get(NodeDetailsKeys.ID.name()));
        }
        if(columnsMap.get(NodeDetailsKeys.HOST.name()) != null) {
            result.setHost(columnsMap.get(NodeDetailsKeys.HOST.name()));
        }
        if(columnsMap.get(NodeDetailsKeys.PORT.name()) != null) {
            result.setPort(Integer.parseInt(columnsMap.get(NodeDetailsKeys.PORT.name())));
        }
        else {
            result.setPort(22);
        }
        if(columnsMap.get(NodeDetailsKeys.NAME.name()) != null) {
            result.setName(columnsMap.get(NodeDetailsKeys.NAME.name()));
        }
        if(columnsMap.get(NodeDetailsKeys.JUMP_HOST.name()) != null) {
            result.setJumpHost(columnsMap.get(NodeDetailsKeys.JUMP_HOST.name()));
        }
        if(columnsMap.get(NodeDetailsKeys.CREDENTIALS_NAME.name()) != null) {
            result.setCredentialName(columnsMap.get(NodeDetailsKeys.CREDENTIALS_NAME.name()));
        }
        if(columnsMap.get(NodeDetailsKeys.STACK.name()) != null) {
            result.setStack(columnsMap.get(NodeDetailsKeys.STACK.name()));
        }
        if(columnsMap.get(NodeDetailsKeys.TAGS.name()) != null) {
            result.setTagNames(parseTags(columnsMap.get(NodeDetailsKeys.TAGS.name())));
        }
        if(columnsMap.get(NodeDetailsKeys.MULTI_FACTOR.name()) != null) {
            result.setMultiFactorAuthenticate(parseMultiFactorMode(columnsMap.get(NodeDetailsKeys.MULTI_FACTOR.name())));
        }
        if(columnsMap.get(NodeDetailsKeys.GROUP_PATH.name()) != null) {
            result.setGroupPaths(parseGroupPath(columnsMap.get(NodeDetailsKeys.GROUP_PATH.name())));
        }
        if(columnsMap.get(NodeDetailsKeys.CLOUD_TYPE.name()) != null) {
            result.setCloudType(parseCloudType(columnsMap.get(NodeDetailsKeys.CLOUD_TYPE.name())));
        }
        if(columnsMap.get(NodeDetailsKeys.TEMP.name()) != null) {
            result.setTemporary(parseTemporaryNodeMode(columnsMap.get(NodeDetailsKeys.TEMP.name())));
        }
        if(columnsMap.get(NodeDetailsKeys.UNIQUE_ID.name()) != null) {
            result.setUniqueId(columnsMap.get(NodeDetailsKeys.UNIQUE_ID.name()));
        }
        if(columnsMap.get(NodeDetailsKeys.DYNAMIC_TAGS.name()) != null) {
            result.setDynamicTags(parseTags(columnsMap.get(NodeDetailsKeys.DYNAMIC_TAGS.name())));
        }

        return result;
    }

    private static NodeGroupInfoList parseGroupPath(String field) {
        NodeGroupInfoList groupList = new NodeGroupInfoList();
		if (field != null && !"".equals(field.trim())) {
			String groups[] = field.split("/");
			if (groups.length > 0) {
				for (String group : groups) {
					groupList.add(new NodeGroupInfo(group, group));
				}
			}
		}
        return groupList;
	}

	private static List<String> parseTags(String tagNames) {
		String names[] = tagNames.split(" ");
		List<String> tagsList = new ArrayList<>();
		for (String name : names) {
			if (!"".equals(name.trim())) {
				tagsList.add(name.trim());
			}
		}
		return tagsList;
	}

    private static CloudType parseCloudType(String cloudTypeName) {
        CloudType type = CloudType.valueOf(cloudTypeName);
        if(type == null) {
            type = CloudType.None;
        }
        return type;
    }

    private static boolean parseTemporaryNodeMode(String field) {
        boolean result = false;
        if(field != null && field.equalsIgnoreCase("Y")) {
            result = true;
        }

        return result;
    }

    private static boolean parseMultiFactorMode(String field) {
        boolean result = false;
        if(field != null && field.equalsIgnoreCase("Y")) {
            result = true;
        }

        return result;
    }
}
