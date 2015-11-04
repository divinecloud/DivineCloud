package com.dc.runbook.rt.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dc.node.NodeDetails;
import com.dc.runbook.dt.domain.RunBook;

public class TransformedRunBook {
	private String	              name;
	private String	              group;
	private String runBookPath;
	
	private List<DtRunbookStep>	  steps;
	private Map<Integer, RunBook>	runBooksMap;
	private List<DtProperty>	  properties;
	private boolean	              utilityMode;

	private String	              generatedPropertiesFilePath;

	private String	              releaseName;
	private String	              releaseVersion;

	private String	              propertiesJson;
	
	private List<NodeDetails> transientNodes;
	
	public List<DtRunbookStep> getSteps() {
		return steps;
	}

	public void setSteps(List<DtRunbookStep> steps) {
		this.steps = steps;
	}

	public Map<Integer, RunBook> getRunBooksMap() {
		return runBooksMap;
	}

	public void setRunBooksMap(Map<Integer, RunBook> runBooksMap) {
		this.runBooksMap = runBooksMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<DtProperty> getProperties() {
		if (properties == null) {
			properties = new ArrayList<>();
		}
		return properties;
	}

	public void setProperties(List<DtProperty> properties) {
		this.properties = properties;
	}

	public boolean isUtilityMode() {
		return utilityMode;
	}

	public void setUtilityMode(boolean utilityMode) {
		this.utilityMode = utilityMode;
	}

	public String getGeneratedPropertiesFilePath() {
		return generatedPropertiesFilePath;
	}

	public void setGeneratedPropertiesFilePath(String generatedPropertiesFilePath) {
		this.generatedPropertiesFilePath = generatedPropertiesFilePath;
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

	public String getPropertiesJson() {
		return propertiesJson;
	}

	public void setPropertiesJson(String propertiesJson) {
		this.propertiesJson = propertiesJson;
	}

	public List<NodeDetails> getTransientNodes() {
		return transientNodes;
	}

	public void setTransientNodes(List<NodeDetails> transientNodes) {
		this.transientNodes = transientNodes;
	}

	public String getRunBookPath() {
		return runBookPath;
	}

	public void setRunBookPath(String runBookPath) {
		this.runBookPath = runBookPath;
	}

}
