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

package com.dc.runbook.dt.domain;

import java.util.List;

import com.dc.runbook.dt.domain.item.ItemType;
import com.dc.runbook.dt.domain.item.RunBookItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RunBookStep {
	private String	       name;
	private String	       description;
	private String	       id;
	private String	       hint;
	private boolean	       idempotent;
	private RunBookItem	   item;
	private ItemType	   itemType;
	private String	       nodeSet;
	private String	       generatedPropertiesFilePath;
	private String	       nodesImportFilePath;
	private boolean	       answersRequired;
	private List<Property>	properties;
	private List<String>	dynamicNodeTags;
	private boolean	       replaceProperties;
	private String	       fileIncludes;
	private String	       fileIncludesDestinationFolder;

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isIdempotent() {
		return idempotent;
	}

	public void setIdempotent(boolean idempotent) {
		this.idempotent = idempotent;
	}

	public RunBookItem getItem() {
		return item;
	}

	public void setItem(RunBookItem item) {
		this.item = item;
	}

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

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(String nodeSet) {
		this.nodeSet = nodeSet;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
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
