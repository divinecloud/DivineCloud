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
