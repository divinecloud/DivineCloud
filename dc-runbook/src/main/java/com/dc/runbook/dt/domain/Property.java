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

package com.dc.runbook.dt.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Property {
	private String	         name;
	private Boolean	         required;
	private String	         defaultValue;
	private String	         multiSelect;
	private String	         description;
	private StepPropertyType	type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isRequired() {
		return required;
	}

	//Note: added a get in addition to isRequired as YamlBeans uses get* instead of is*
	@JsonIgnore
	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

    public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(String multiSelect) {
		this.multiSelect = multiSelect;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StepPropertyType getType() {
		return type;
	}

	public void setType(StepPropertyType type) {
		this.type = type;
	}

}
