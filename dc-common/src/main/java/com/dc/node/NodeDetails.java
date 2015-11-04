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

import java.util.List;

public class NodeDetails {
    private String displayId;
    private String host;
    private int port;
    private String name;
    private String jumpHost;
    private String credentialName;
    private String stack;
    private List<String> tagNames;
    private boolean multiFactorAuthenticate;
    private NodeGroupInfoList groupPaths;
    private CloudType cloudType;
    private String uniqueId;
    private boolean temporary;
    private List<String> dynamicTags;

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJumpHost() {
        return jumpHost;
    }

    public void setJumpHost(String jumpHost) {
        this.jumpHost = jumpHost;
    }

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public CloudType getCloudType() {
		return cloudType;
	}

	public void setCloudType(CloudType cloudType) {
		this.cloudType = cloudType;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public List<String> getDynamicTags() {
        return dynamicTags;
    }

    public void setDynamicTags(List<String> dynamicTags) {
        this.dynamicTags = dynamicTags;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NodeDetails other = (NodeDetails) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        return true;
    }

    public boolean isMultiFactorAuthenticate() {
        return multiFactorAuthenticate;
    }

    public void setMultiFactorAuthenticate(boolean multiFactorAuthenticate) {
        this.multiFactorAuthenticate = multiFactorAuthenticate;
    }

    public NodeGroupInfoList getGroupPaths() {
		return groupPaths;
	}

	public void setGroupPaths(NodeGroupInfoList groupPaths) {
		this.groupPaths = groupPaths;
	}

    @Override
    public String toString() {
        return "NodeDetails{" +
                "displayId='" + displayId + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", jumpHost='" + jumpHost + '\'' +
                ", credentialName='" + credentialName + '\'' +
                ", stack='" + stack + '\'' +
                ", tagNames=" + tagNames +
                ", multiFactorAuthenticate=" + multiFactorAuthenticate +
                ", groupPaths=" + groupPaths +
                ", cloudType=" + cloudType +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }
}
