package com.dc.runbook.rt.exec.support;

public class ColorCodeFilter {

    public static String filter(String text) {
        System.out.println(text);
        if(text != null) {
            text = text.replaceAll("\\[\\d*;\\d*m", ""); //Removes any color codes from the output.
            text = text.replaceAll("\\[\\d*m", ""); //Removes any color codes from the output.
        }
        return text;
    }
}
