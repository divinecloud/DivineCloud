package com.dc.runbook.rt.exec.support;

import com.dc.LinuxOSType;
import com.dc.support.KeyValuePair;
import com.dc.util.string.EnhancedStringBuilder;

import java.util.List;

public class PropertiesReplacer {

    public static String replace(String source, List<KeyValuePair<String, String>> properties, List<KeyValuePair<String, String>> runBookProperties, boolean allow) {
        String result = source;
        if(allow) {
            if (source != null && !"".equals(source.trim())) {
                EnhancedStringBuilder builder = new EnhancedStringBuilder(new StringBuilder(source));
                if (properties != null) {
                    for (KeyValuePair<String, String> pair : properties) {
                        builder.replaceAll(pair.getKey(), pair.getValue());
                    }
                }
                if (runBookProperties != null) {
                    for (KeyValuePair<String, String> pair : runBookProperties) {
                        builder.replaceAll(pair.getKey(), pair.getValue());
                    }
                }

                result = builder.toString();
            }
        }

        return result;
    }

    public static List<KeyValuePair<String, LinuxOSType>> replace(List<KeyValuePair<String, LinuxOSType>> source, List<KeyValuePair<String, String>> properties, List<KeyValuePair<String, String>> runBookProperties, boolean allow) {
        if(allow) {
            for (KeyValuePair<String, LinuxOSType> pair : source) {
                EnhancedStringBuilder builder = new EnhancedStringBuilder(new StringBuilder(pair.getKey()));
                if (properties != null) {
                    for (KeyValuePair<String, String> propertiesPair : properties) {
                        builder.replaceAll(propertiesPair.getKey(), propertiesPair.getValue());
                    }
                }
                if (runBookProperties != null) {
                    for (KeyValuePair<String, String> propertiesPair : runBookProperties) {
                        builder.replaceAll(propertiesPair.getKey(), propertiesPair.getValue());
                    }
                }

                pair.setKey(builder.toString());
            }
        }
        return source;
    }

}
