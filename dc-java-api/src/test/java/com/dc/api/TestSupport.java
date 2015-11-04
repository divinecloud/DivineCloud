package com.dc.api;

import java.io.IOException;
import java.util.Properties;

/**
 * Support class for tests code.
 */
public class TestSupport {

    public static String getProperty(String key) {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/dc/api/test.properties"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Test properties file 'com/dc/api/test.properties' Not Found");
        }
        return properties.getProperty(key);
    }

}
