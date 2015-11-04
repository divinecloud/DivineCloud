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
