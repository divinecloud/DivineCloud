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

package com.dc.ssh.client.support.callback;

import java.util.ArrayList;
import java.util.List;

public class BufferedLocalCallback implements LocalCallback {
    private List<byte[]> outputBytes;

    private List<byte[]> errorBytes;

    private int status = -99999999;

    private boolean cancelled;

    public BufferedLocalCallback() {
        outputBytes = new ArrayList<>(2);
        errorBytes = new ArrayList<>(2);
    }

    public void output(byte[] output) {
        outputBytes.add(output);
    }

    public void error(byte[] error) {
        errorBytes.add(error);
    }

    public void done(int status) {
        this.status = status;
    }

    public int status() {
        return status;
    }

    @Override
    public void executionCancelled() {
        cancelled = true;
    }

    public byte[] getOutput() {
        return cloneBytes(outputBytes);
    }

    public byte[] getError() {
        return cloneBytes(errorBytes);
    }

    private int calculateBytesSize(List<byte[]> bytesList) {
        int result = 0;
        for(byte[] bytes : bytesList) {
            result += bytes.length;
        }
        return result;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    private byte[] cloneBytes(List<byte[]> bytesList) {
        int size = calculateBytesSize(bytesList);
        byte[] result = new byte[size];
        int index = 0;
        for(byte[] bytes:bytesList) {
            System.arraycopy(bytes, 0, result, index, bytes.length);
            index +=bytes.length;
        }
        return result;
    }
}