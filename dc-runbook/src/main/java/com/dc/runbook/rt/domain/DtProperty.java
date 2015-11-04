package com.dc.runbook.rt.domain;

import com.dc.runbook.dt.domain.Property;

public class DtProperty {
	private Property stepProperty;
	private String	     runBookName;
	private String	     value;

	public DtProperty() {
	    super();
    }

	public DtProperty(Property stepProperty, String value, String runBookName) {
	    super();
	    this.stepProperty = stepProperty;
	    this.value = value;
	    this.runBookName = runBookName;
    }

	public Property getStepProperty() {
		return stepProperty;
	}

	public void setStepProperty(Property stepProperty) {
		this.stepProperty = stepProperty;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRunBookName() {
		return runBookName;
	}

	public void setRunBookName(String runBookName) {
		this.runBookName = runBookName;
	}

}
