package com.dc.ssh.utils;

import com.dc.util.string.EnhancedStringBuilder;

public class TextSupport {

    public static String generateSaveTextScript(String filePath, String text, boolean append) {
        text = text.replaceAll("\\$", "\\\\$");
        text = text.replaceAll("`", "\\`");
        text = text.replaceAll("!", "\\!");
        text = text.replaceAll("\\\"", "\\\\\"");
        StringBuilder result = new StringBuilder();
        if(text != null && text.length() > 0 && filePath != null && filePath.trim().length() > 0) {
            String[] lines = text.split("\n");
            boolean appendMarked = false;
            for(String line : lines) {
                result.append("echo ").append("\"").append(line).append("\"");
                if(!appendMarked) {
                    if (append) {
                        result.append(" >> ");
                    } else {
                        result.append(" > ");
                    }
                    appendMarked = true;
                }
                else {
                    result.append(" >> ");
                }
                result.append(filePath).append('\n');
            }
        }
        return result.toString();
    }

    public static void main(String [] args) {
        String sample = "bucket_name = \"python-sdk-sample-%s\" % uuid.uuid4()";
        EnhancedStringBuilder eBuilder = new EnhancedStringBuilder(new StringBuilder(sample));
        eBuilder.replaceAll("$", "\\\\$");
        eBuilder.replaceAll("`", "\\`");
        eBuilder.replaceAll("!", "\\!");
        eBuilder.replaceAll("\\\"", "\\\\\"");
        String text = eBuilder.toString();
        System.out.println(text);
        sample = sample.replaceAll("\\\"", "\\\\\"");
        sample = sample.replaceAll("\\$", "\\\\$");
        sample = sample.replaceAll("`", "\\`");
        sample = sample.replaceAll("!", "\\!");
        System.out.println(sample);


    }
}

