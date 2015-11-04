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

package com.dc.ssh.client.support;

/**
 * Thread pool configuration details.
 */
public class ThreadPoolConfiguration {
    private int minThreads;
    private int maxThreads;
    private int keepAliveTime;

    public ThreadPoolConfiguration(int minThreads, int maxThreads, int keepAliveTime) {
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.keepAliveTime = keepAliveTime;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public boolean validate() {
        boolean valid = true;
        if(minThreads <= 0 || minThreads > maxThreads || keepAliveTime <= 0) {
            valid = false;
        }
        return valid;
    }

    public String toString() {
        return "ThreadPoolConfiguration - MinThreads : " + minThreads + " MaxThreads : " + maxThreads + " keepAliveTime : " + keepAliveTime;
    }
}
