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

package com.dc.runbook.rt.domain;

import com.dc.node.NodeDetails;

import java.util.List;

public class DtRunbook {
	private String	          userId;
	private String	          spaceId;
    private String id;
	private List<DtRunbookStep>	steps;
    private List<DtProperty> properties;
    private boolean         utilityMode;
    private String	          generatedPropertiesFilePath;
    private String propertiesJson;
    private String runBookPath;
    private List<NodeDetails> transientNodes;
    
    public DtRunbook(String userId, String spaceId, String id, List<DtRunbookStep> steps, List<DtProperty> properties, boolean utilityMode, String	generatedPropertiesFilePath, String propertiesJson, String runBookPath, List<NodeDetails> transientNodes) {
        this.userId = userId;
        this.spaceId = spaceId;
        this.id = id;
        this.steps = steps;
        this.properties = properties;
        this.utilityMode = utilityMode;
        this.generatedPropertiesFilePath = generatedPropertiesFilePath;
        this.propertiesJson = propertiesJson;
        this.runBookPath = runBookPath;
        this.transientNodes = transientNodes;
    }

    public String getUserId() {
		return userId;
	}

    public List<DtRunbookStep> getSteps() {
        return steps;
    }

    public String getId() {
        return id;
    }

	public String getSpaceId() {
		return spaceId;
	}

    public List<DtProperty> getProperties() {
        return properties;
    }

    public boolean isUtilityMode() {
        return utilityMode;
    }

    public String getGeneratedPropertiesFilePath() {
        return generatedPropertiesFilePath;
    }

	public String getPropertiesJson() {
		return propertiesJson;
	}

	public String getRunBookPath() {
		return runBookPath;
	}

    public List<NodeDetails> getTransientNodes() {
        return transientNodes;
    }
}
