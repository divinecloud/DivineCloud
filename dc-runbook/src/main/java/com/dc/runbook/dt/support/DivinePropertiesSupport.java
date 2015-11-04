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
