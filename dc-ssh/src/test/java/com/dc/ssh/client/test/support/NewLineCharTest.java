/*
 *
 *  * Copyright (C) 2014 Divine Cloud Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
