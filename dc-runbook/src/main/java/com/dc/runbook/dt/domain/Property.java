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
