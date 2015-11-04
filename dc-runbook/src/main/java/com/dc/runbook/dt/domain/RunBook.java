package com.dc.runbook.dt.domain;

import java.util.List;

public class RunBook {
	private String	          name;
	private String	          description;
	private String	          summary;
	private String	          references;
	private String	          preRequisite;
	private String	          hardwareRequirements;
	private String	          supportedOs;
	private String	          version;
	private String	          schemaVersion;
	private String	          authors;
	private String	          createDate;
	private String	          lastUpdateDate;
	private List<RunBookStep>	steps;
	private List<Property>	  properties;
	private boolean	          utilityMode;
	private int	              sequence;

	private boolean	          groupSteps;
	private String	          stepGroupsName;

	private String	          generatedPropertiesFilePath;

	private String successfulCompletionMessage;

	private String	          releaseName;
	private String	          releaseVersion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPreRequisite() {
		return preRequisite;
	}

	public void setPreRequisite(String preRequisite) {
		this.preRequisite = preRequisite;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public List<RunBookStep> getSteps() {
		return steps;
	}

	public void setSteps(List<RunBookStep> steps) {
		this.steps = steps;
	}

	public String getHardwareRequirements() {
		return hardwareRequirements;
	}

	public void setHardwareRequirements(String hardwareRequirements) {
		this.hardwareRequirements = hardwareRequirements;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	// public List<RunbookImageLink> getImagelinks() {
	// return imagelinks;
	// }
	//
	// public void setImagelinks(List<RunbookImageLink> imagelinks) {
	// this.imagelinks = imagelinks;
	// }

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public boolean isUtilityMode() {
		return utilityMode;
	}

	public void setUtilityMode(boolean utilityMode) {
		this.utilityMode = utilityMode;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getSupportedOs() {
		return supportedOs;
	}

	public void setSupportedOs(String supportedOs) {
		this.supportedOs = supportedOs;
	}

	public boolean isGroupSteps() {
		return groupSteps;
	}

	public void setGroupSteps(boolean groupSteps) {
		this.groupSteps = groupSteps;
	}

	public String getStepGroupsName() {
		return stepGroupsName;
	}

	public void setStepGroupsName(String stepGroupsName) {
		this.stepGroupsName = stepGroupsName;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public String getGeneratedPropertiesFilePath() {
		return generatedPropertiesFilePath;
	}

	public void setGeneratedPropertiesFilePath(String generatedPropertiesFilePath) {
		this.generatedPropertiesFilePath = generatedPropertiesFilePath;
	}

	public RunBookStep findById(String stepId) {
		RunBookStep result = null;
		for (RunBookStep step : steps) {
			if (stepId.equals(step.getId())) {
				result = step;
				break;
			}
		}

		return result;
	}

	public String getSuccessfulCompletionMessage() {
		return successfulCompletionMessage;
	}

	public void setSuccessfulCompletionMessage(String successfulCompletionMessage) {
		this.successfulCompletionMessage = successfulCompletionMessage;
	}

	public String getReleaseName() {
		return releaseName;
	}

	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}

	public String getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

}
