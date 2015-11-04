package com.dc.ssh.client.support;


import com.dc.LinuxOSType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OSTypeParserTest {

    @Test
    public void testParseCentOS() {
        String osTypeDetails = "CentOS Linux release 7.0.1406 (Core)                                                                                                         \n" +
                "NAME=\"CentOS Linux\"                                                                                                                          \n" +
                "VERSION=\"7 (Core)\"                                                                                                                           \n" +
                "ID=\"centos\"                                                                                                                                  \n" +
                "ID_LIKE=\"rhel fedora\"                                                                                                                        \n" +
                "VERSION_ID=\"7\"                                                                                                                               \n" +
                "PRETTY_NAME=\"CentOS Linux 7 (Core)\"                                                                                                          \n" +
                "ANSI_COLOR=\"0;31\"                                                                                                                            \n" +
                "CPE_NAME=\"cpe:/o:centos:centos:7\"                                                                                                            \n" +
                "HOME_URL=\"https://www.centos.org/\"                                                                                                           \n" +
                "BUG_REPORT_URL=\"https://bugs.centos.org/\"                                                                                                    \n" +
                "                             ";

        LinuxOSType type = OSTypeParser.parse(osTypeDetails);
        assertEquals(LinuxOSType.CentOS, type);
    }

    @Test
    public void testParseAmazonLinux() {
        String osTypeDetails = "NAME=\"Amazon Linux AMI\"                                                                                                                      \n" +
                "VERSION=\"2014.09\"                                                                                                                            \n" +
                "ID=\"amzn\"                                                                                                                                    \n" +
                "ID_LIKE=\"rhel fedora\"                                                                                                                        \n" +
                "VERSION_ID=\"2014.09\"                                                                                                                         \n" +
                "PRETTY_NAME=\"Amazon Linux AMI 2014.09\"                                                                                                       \n" +
                "ANSI_COLOR=\"0;33\"                                                                                                                            \n" +
                "CPE_NAME=\"cpe:/o:amazon:linux:2014.09:ga\"                                                                                                    \n" +
                "HOME_URL=\"http://aws.amazon.com/amazon-linux-ami/\"       ";

        LinuxOSType type = OSTypeParser.parse(osTypeDetails);
        assertEquals(LinuxOSType.Amazon, type);

    }

    @Test
    public void testParseUbuntu() {
        String osTypeDetails = "NAME=\"Ubuntu\"                                                                                                                                \n" +
                "VERSION=\"14.04.1 LTS, Trusty Tahr\"                                                                                                           \n" +
                "ID=ubuntu                                                                                                                                    \n" +
                "ID_LIKE=debian                                                                                                                               \n" +
                "PRETTY_NAME=\"Ubuntu 14.04.1 LTS\"                                                                                                             \n" +
                "VERSION_ID=\"14.04\"                                                                                                                           \n" +
                "HOME_URL=\"http://www.ubuntu.com/\"                                                                                                            \n" +
                "SUPPORT_URL=\"http://help.ubuntu.com/\"                                                                                                        \n" +
                "BUG_REPORT_URL=\"http://bugs.launchpad.net/ubuntu/\"            ";

        LinuxOSType type = OSTypeParser.parse(osTypeDetails);
        assertEquals(LinuxOSType.Ubuntu, type);

    }
}
