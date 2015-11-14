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

package com.dc.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpAddressFetcher {

    public static String getHostName() throws SocketException {
        String hostname = null;
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            if (!ip.isLoopbackAddress() && !ip.isLinkLocalAddress()) {
                hostname = ip.getHostName();
            }
        } catch (UnknownHostException e) {
            hostname = getIpAddress();
        }
        return hostname;
    }

    public static String getIpAddress() throws SocketException {
        String ipAddress = null;
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            if (!ip.isLoopbackAddress() && !ip.isLinkLocalAddress()) {
                ipAddress = ip.getHostAddress();
            }
        } catch (UnknownHostException e) {
            // do nothing
        }
        if (ipAddress == null) {
            List<InetAddress> addresses = new ArrayList<InetAddress>();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                Enumeration<InetAddress> e2 = ni.getInetAddresses();
                while (e2.hasMoreElements()) {
                    addresses.add(e2.nextElement());
                }
            }

            for (InetAddress iaddresses : addresses) {
                if (!iaddresses.isLoopbackAddress() && !iaddresses.isLinkLocalAddress()) {
                    ipAddress = iaddresses.getHostAddress();
                    break;
                }
            }
        }

        if (ipAddress == null || ipAddress.trim().equals("")) {
            throw new RuntimeException("Unable to retrieve IP Address for the machine.");
        }

        return ipAddress;
    }

}
