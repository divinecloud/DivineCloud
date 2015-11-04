package com.dc.ssh.client.test.support;

import com.dc.ssh.client.SshClientConfiguration;
import com.dc.ssh.client.SshException;
import com.dc.ssh.client.builder.SshClientBuilder;
import com.dc.ssh.client.exec.SshClient;
import com.dc.exec.ExecutionDetails;
import com.dc.ssh.client.support.SshClientConstants;

import java.io.IOException;
import java.util.Properties;

import static com.dc.ssh.client.support.SshClientConstants.PATH_SEPARATOR;
import static com.dc.ssh.client.support.SshClientConstants.SSH_OUTPUT_FOLDER;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Support class for tests code.
 */
public class TestSupport {
    public static String trimCrLf(String value) {
        int carriageFeedIndex = value.indexOf('\r');

        if (carriageFeedIndex != -1) {
            value = value.substring(0, carriageFeedIndex);
        } else {
            int newlineIndex = value.indexOf('\n');

            if (newlineIndex != -1) {
                value = value.substring(0, newlineIndex);
            }
        }

        return value;
    }

    public static String getProperty(String key) {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/dc/ssh/client/test.properties"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Test properties file 'com/dc/ssh/client/test.properties' Not Found");
        }
        return properties.getProperty(key);
    }

    public static void validateCachedOutputWithPrunedProcessId(byte[] output, String cmdExecId, SshClientConfiguration configuration) throws IOException, SshException {
        String outputFilePath = configuration.getCachedFilesPath() + PATH_SEPARATOR + SSH_OUTPUT_FOLDER + PATH_SEPARATOR + cmdExecId;
        SshClient client = SshClientBuilder.build(SshTestObjectsGenerator.createNodeCredentials(), configuration);
        ExecutionDetails details = client.execute("cat " + outputFilePath + SshClientConstants.PATH_SEPARATOR + SshClientConstants.SSH_OUTPUT_FILE);
        assertNotNull(details);
        byte[] detailsOutput = pruneProcessId(details.getOutput());
        if(output.length != detailsOutput.length) {
            System.out.println("output length : " + output.length + "  details output length : " + detailsOutput.length);
            fail("cached file output does not match with the received output");
        }
        for(int i=0; i<output.length; i++) {
            if(output[i] != detailsOutput[i]) {
                fail("cached file output does not match with the received output");
            }
        }
    }


    public static void validateCachedOutput(byte[] output, String cmdExecId, SshClientConfiguration configuration) throws IOException, SshException {
        String outputFilePath = configuration.getCachedFilesPath() + PATH_SEPARATOR + SSH_OUTPUT_FOLDER + PATH_SEPARATOR + cmdExecId;
        SshClient client = SshClientBuilder.build(SshTestObjectsGenerator.createNodeCredentials(), configuration);
        ExecutionDetails details = client.execute("cat " + outputFilePath + SshClientConstants.PATH_SEPARATOR + SshClientConstants.SSH_OUTPUT_FILE);
        assertNotNull(details);
        byte[] detailsOutput = details.getOutput();
        if(output.length != detailsOutput.length) {
            System.out.println("output length : " + output.length + "  details output length : " + detailsOutput.length);
            fail("cached file output does not match with the received output");
        }
        for(int i=0; i<output.length; i++) {
            if(output[i] != detailsOutput[i]) {
                fail("cached file output does not match with the received output");
            }
        }
    }

    public static byte[] pruneProcessId(byte[] output) {
        byte[] result;
        int newLineIndex = -1;
        for(int i = 0; i<output.length; i++) {
            if(output[i] == (byte)'\n') {
                newLineIndex = i;
                break;
            }
        }
        int resultLength = output.length - (newLineIndex + 1);
        result = new byte[resultLength];
        System.arraycopy(output, (newLineIndex + 1), result, 0, resultLength);
        return result;
    }

    public static boolean equals(byte[] one, byte[] other) {
        boolean result = false;
        if(one != null && other != null) {
            if(one.length == other.length) {
                for(int i=0; i<one.length; i++) {
                    if(one[i] != other[i]) {
                        break;
                    }
                }
                result = true;
            }
        }
        else {
            result = true;
        }
        return result;
    }
}