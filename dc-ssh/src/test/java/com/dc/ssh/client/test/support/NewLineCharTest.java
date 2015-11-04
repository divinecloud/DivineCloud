package com.dc.ssh.client.test.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhupen on 7/21/14.
 */
public class NewLineCharTest {

    public static void parseProcessId(byte[] processIdBytes) {
        List<Byte> pIdBytes = new ArrayList<>();
        for(int i=0; i<processIdBytes.length; i++) {
            if(processIdBytes[i] == (byte)'\n') {
                break;
            }
            else {
                pIdBytes.add(processIdBytes[i]);
            }
        }

        System.out.println(new String(convertToByteArray(pIdBytes)));
    }

    private static byte[] convertToByteArray(List<Byte> src) {
        byte[] result = new byte[src.size()];
        for(int i=0; i<src.size(); i++) {
            result[i] = src.get(i);
        }
        return result;
    }

    public static void main(String [] args) {
        String str = "4356" + '\n' + "23";
        parseProcessId(str.getBytes());
    }
}
