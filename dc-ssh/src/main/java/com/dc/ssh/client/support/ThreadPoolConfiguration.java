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
