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

package com.dc.runbook.dt.support;

import java.io.InputStream;
import java.util.Properties;

import com.dc.DcLoggerFactory;

public class DivinePropertiesSupport {
	private static DivinePropertiesSupport instance;

	private static Properties properties;

	private DivinePropertiesSupport() {
		loadProperties();
	}
	
	public synchronized static DivinePropertiesSupport getInstance() {
		if(instance == null) {
			instance = new DivinePropertiesSupport();
		}
		return instance;
	}

	public String getValue(String key) {
		return (String) properties.get(key);
	}
	
	private static void loadProperties() {
		properties = new Properties();
		InputStream in = DivinePropertiesSupport.class.getClassLoader().getResourceAsStream("/divine.properties");
		try {
			properties.load(in);
		} catch (Exception e) {
			DcLoggerFactory.getInstance().getLogger().error("Error occurred while loading divine.properties.", e);
			properties = null;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				DcLoggerFactory.getInstance().getLogger().error("Error occurred while closing input stream for divine.properties.", e);
			}
		}
	}


}
