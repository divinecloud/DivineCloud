package com.dc.support;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Loads the properties from the properties file into Properties object.
 */
public class PropertiesSupport {
    public static Properties load(String propertyFile) throws UserInputException {
        Properties properties;
        InputStream is = PropertiesSupport.class.getClassLoader().getResourceAsStream(propertyFile);
        if (is == null) {
            // @TODO: Replace with appropriate exception
            throw new UserInputException("Properties file " + propertyFile + " not found under classpath");
        }
        try {
            properties = new Properties();
            BufferedInputStream bis = new BufferedInputStream(is);
            properties.load(bis);
            properties = trim(properties);
        } catch (IOException e) {
            throw new UserInputException(e);
        }
        // catch IO and throw userInputException
        finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new UserInputException(e);
            }
        }

        return properties;

    }

    private static Properties trim(Properties properties) {
        Properties result = new Properties();
        Set<String> propertyNames = properties.stringPropertyNames();
        Iterator<String> iterator = propertyNames.iterator();
        if (iterator != null) {
            while (iterator.hasNext()) {
                String propertyName = iterator.next();
                String value = properties.getProperty(propertyName);
                if (value != null) {
                    value = value.trim();
                }
                result.put(propertyName, value);
            }
        }
        return result;
    }

    public static Properties loadFromPath(String propertyFile) throws IOException {
        Properties properties = new Properties();
        if (new File(propertyFile).exists()) {
            properties.load(new FileInputStream(propertyFile));
        } else {
            throw new IOException("Properties file " + propertyFile + " not found in path");
        }
        return properties;

    }

}
