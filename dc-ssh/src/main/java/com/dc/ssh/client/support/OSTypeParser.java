package com.dc.ssh.client.support;

import com.dc.LinuxOSType;

import java.util.HashMap;
import java.util.Map;

public class OSTypeParser {
    private static Map<String, LinuxOSType> linuxOSTypeMap;

    static {
        linuxOSTypeMap = new HashMap<>();
        linuxOSTypeMap.put("\"amzn\"", LinuxOSType.Amazon);
        linuxOSTypeMap.put("\"centos\"", LinuxOSType.CentOS);
        linuxOSTypeMap.put("rhel", LinuxOSType.RedHat);
        linuxOSTypeMap.put("suse", LinuxOSType.SuSe);
        linuxOSTypeMap.put("ubuntu", LinuxOSType.Ubuntu);
        linuxOSTypeMap.put("debian", LinuxOSType.Debian);
        linuxOSTypeMap.put("fedora", LinuxOSType.Fedora);
        linuxOSTypeMap.put("gentoo", LinuxOSType.Gentoo);
        linuxOSTypeMap.put("opensuse", LinuxOSType.OpenSuSe);
        linuxOSTypeMap.put("coreos", LinuxOSType.CoreOS);
        //@TODO: Later verify the ID are true for all the OS Types. For now only a few ones are verified, rest needs verification.
    }

    public static LinuxOSType parse(String osTypeDetails) {
        LinuxOSType result = null;
        String osName = null;
        String[] lines = osTypeDetails.split("\n");
        for(String line : lines) {
            String[] words = line.split("=");
            if(words.length > 1) {
                if(words[0].trim().equalsIgnoreCase("ID")) {
                    osName = words[1].trim();
                    break;
                }
            }
            else if(words.length == 1) {
                String upperCaseText = words[0].toUpperCase();
                if(upperCaseText.contains("SUSE")) {
                    osName = "suse";
                }
                else if(upperCaseText.contains("FEDORA")) {
                    osName = "fedora";
                }
            }
        }
        if(osName != null && osName.length() > 0) {
            result = linuxOSTypeMap.get(osName);
        }
        if(result == null) {
            result = LinuxOSType.Any;
        }
        return result;
    }
}
