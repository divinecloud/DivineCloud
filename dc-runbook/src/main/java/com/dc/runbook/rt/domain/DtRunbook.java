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
