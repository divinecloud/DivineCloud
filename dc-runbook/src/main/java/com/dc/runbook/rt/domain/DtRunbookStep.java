package com.dc.runbook.rt.domain;

import java.util.List;

import com.dc.runbook.rt.domain.item.DtRunbookItem;

public class DtRunbookStep {
	private String	         name;
	private String	         id;
	private String	         nodeSet;
	private boolean	         continueOnError;
	private boolean	         skip;
	private boolean	         pauseHere;
	private boolean	         answersRequired;
	private DtRunbookItem	 item;
	private List<Integer>	 parentRunbooksId;
	private String	         runAs;
	private String	         password;
	private boolean	         admin;
	private int	             sequenceId;
	private List<DtProperty>	properties;
	private String	         generatedPropertiesFilePath;
	private String	         nodesImportFilePath;
    private List<GeneratedProperty> generatedProperties;
    private List<String> dynamicNodeTags;

    private boolean replaceProperties;

	private String	         fileIncludes;
	private String	         fileIncludesDestinationFolder;

	public boolean isContinueOnError() {
		return continueOnError;
	}

	public void setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public boolean isPauseHere() {
		return pauseHere;
	}

	public void setPauseHere(boolean pauseHere) {
		this.pauseHere = pauseHere;
	}

	public DtRunbookItem getItem() {
		return item;
	}

	public void setItem(DtRunbookItem item) {
		this.item = item;
	}

	public List<Integer> getParentRunbooksId() {
		return parentRunbooksId;
	}

	public void setParentRunbooksId(List<Integer> parentRunbooksId) {
		this.parentRunbooksId = parentRunbooksId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRunAs() {
		return runAs;
	}

	public void setRunAs(String runAs) {
		this.runAs = runAs;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<DtProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<DtProperty> properties) {
		this.properties = properties;
	}

	public String getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(String nodeSet) {
		this.nodeSet = nodeSet;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getGeneratedPropertiesFilePath() {
		return generatedPropertiesFilePath;
	}

	public void setGeneratedPropertiesFilePath(String generatedPropertiesFilePath) {
		this.generatedPropertiesFilePath = generatedPropertiesFilePath;
	}

	public boolean isAnswersRequired() {
		return answersRequired;
	}

	public void setAnswersRequired(boolean answersRequired) {
		this.answersRequired = answersRequired;
	}

	public List<GeneratedProperty> getGeneratedProperties() {
		return generatedProperties;
	}

	public void setGeneratedProperties(List<GeneratedProperty> generatedProperties) {
		this.generatedProperties = generatedProperties;
	}

	public String getNodesImportFilePath() {
		return nodesImportFilePath;
	}

	public void setNodesImportFilePath(String nodesImportFilePath) {
		this.nodesImportFilePath = nodesImportFilePath;
	}

	public boolean isReplaceProperties() {
		return replaceProperties;
	}

	public void setReplaceProperties(boolean replaceProperties) {
		this.replaceProperties = replaceProperties;
	}

	public List<String> getDynamicNodeTags() {
		return dynamicNodeTags;
	}

	public void setDynamicNodeTags(List<String> dynamicNodeTags) {
		this.dynamicNodeTags = dynamicNodeTags;
	}

	public String getFileIncludes() {
		return fileIncludes;
	}

	public void setFileIncludes(String fileIncludes) {
		this.fileIncludes = fileIncludes;
	}

	public String getFileIncludesDestinationFolder() {
		return fileIncludesDestinationFolder;
	}

	public void setFileIncludesDestinationFolder(String fileIncludesDestinationFolder) {
		this.fileIncludesDestinationFolder = fileIncludesDestinationFolder;
	}
}
