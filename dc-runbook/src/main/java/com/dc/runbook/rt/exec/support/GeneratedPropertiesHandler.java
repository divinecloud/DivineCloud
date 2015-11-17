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

    public void addGeneratedRunBookProperties(GeneratedPropertiesHandler stepGeneratedPropertiesHandler) {
        List<KeyValuePair<String, String>> stepGeneratedProperties = stepGeneratedPropertiesHandler.retrieveGeneratedProperties();
        if (stepGeneratedProperties != null && stepGeneratedProperties.size() > 0) {
            for (KeyValuePair<String, String> prop : stepGeneratedProperties) {
                addProperty(prop.getKey(), prop.getValue());
            }
        }
    }
}

