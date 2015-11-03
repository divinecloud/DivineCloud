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

package com.dc.ssh.utils;

import com.dc.support.KeyValuePair;

import java.util.List;

public class SedSupport {

    public static String createSedScript(List<KeyValuePair<String, String>> properties, String filePath) {
        StringBuilder result = new StringBuilder();
        if(properties != null && filePath != null && filePath.length() > 0) {
            result.append("sed -i.bak ' {").append('\n');
            for(KeyValuePair<String, String> pair : properties) {
                result.append("s/");
                result.append(escapeText(pair.getKey()));
                result.append("/");
                result.append(escapeText(pair.getValue()));
                result.append("/");
                result.append('\n');
            }
            result.append(" } ' ").append(filePath);
        }
        else {
            throw new SshUtilsException("invalid arguments provided for generating sed script. filePath : " + filePath + " Properties : " + properties);
        }
        return result.toString();
    }

    private static String escapeText(String text) {
        text = text.replaceAll("\n", "\\\\n");
        text = text.replaceAll("/", "\\\\/");
        return text;
    }
}
