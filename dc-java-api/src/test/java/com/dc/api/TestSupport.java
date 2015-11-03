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
