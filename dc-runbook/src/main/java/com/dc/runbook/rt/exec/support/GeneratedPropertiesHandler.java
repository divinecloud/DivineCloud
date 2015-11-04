package com.dc.runbook.rt.exec.support;

import com.dc.support.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeneratedPropertiesHandler {
    private Map<String, String> generatedPropertiesMap;

    public GeneratedPropertiesHandler() {
        generatedPropertiesMap = new ConcurrentHashMap<>();
    }

    public void addProperty(String name, String value) {
        synchronized (name.intern()) {
            if (generatedPropertiesMap.containsKey(name)) {
                generatedPropertiesMap.put(name, generatedPropertiesMap.get(name) + "," + value);
            } else {
                generatedPropertiesMap.put(name, value);
            }
        }
    }

    public synchronized List<KeyValuePair<String, String>> retrieveGeneratedProperties() {
        List<KeyValuePair<String, String>> result = new ArrayList<>();
        generatedPropertiesMap.forEach((key, value) -> {
            KeyValuePair<String, String> property = new KeyValuePair<>();
            property.setKey(key);
            property.setValue(value);
            result.add(property);

        });
        return result;
    }
}

