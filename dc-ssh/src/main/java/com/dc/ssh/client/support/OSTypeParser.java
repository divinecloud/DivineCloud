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
