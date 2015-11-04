package com.dc.ssh.client;

import com.dc.ssh.client.exec.vo.ShellType;
import com.dc.ssh.client.support.ThreadPoolConfiguration;

import java.util.Date;

/**
 * Ssh client configuration.
 */
public class SshClientConfiguration {
    private int totalConnectAttempts;
    private int connectWaitTime;
    private int readTimeout;
    private int readLatency;
    private boolean ptySupport;
    private int bufferSize;
    private String uniqueId;
    private String cachedFilesPath;
    private boolean localCacheAllowed;
    private ShellType shellType;
    private ThreadPoolConfiguration threadPoolConfiguration;
    private short idleConnectCheckTimeInMinutes;

    public static class Builder {
        private int totalConnectAttempts = 3; //default value is 3.
        private int connectWaitTime = 1000 * 5; //default value is 5 seconds
        private int readTimeout = 1000* 60 * 30; //default value is 30 minutes.
        private int readLatency = 1000 * 3; //default value is 3 secs.
        private boolean ptySupport = true; //default value is true.
        private int bufferSize = 1024; // amount of bytes to read/write at a time
        private String cachedFilesPath = "/tmp"; //default location to cache temp files is /tmp
        private boolean localCacheAllowed;
        private ShellType shellType = ShellType.BASH; //default shell type
        private short idleConnectCheckTimeInMinutes = 10; //default value is 10 minutes

        private String uniqueId = "DTerm"+(int)(Math.random() * 1000000000) + new Date().getTime(); //default value for unique id
        private ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration(8, 8, 2);

        public Builder() {
        }

        public Builder totalConnectAttempts(int count) {
            totalConnectAttempts = count;
            return this;
        }

        public Builder connectWaitTime(int time) {
            connectWaitTime = time;
            return this;
        }

        public Builder readTimeout(int timeInMillis) {
            readTimeout = timeInMillis;
            return this;
        }

        public Builder readLatency(int timeInMillis) {
            readLatency = timeInMillis;
            return this;
        }

        public Builder ptySupport(boolean supported) {
            ptySupport = supported;
            return this;
        }

        public Builder bufferSize(int size) {
            bufferSize = size;
            return this;
        }

        public Builder uniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder threadPool(ThreadPoolConfiguration configuration) {
            this.threadPoolConfiguration = configuration;
            return this;
        }

        public Builder defaultShellType(ShellType type) {
            this.shellType = type;
            return this;
        }

        public Builder cacheLocally(String cachedFilesPath) {
            localCacheAllowed = true;
            this.cachedFilesPath = cachedFilesPath;
            return this;
        }

        public Builder idleConnectCheckTimeInMinutes(short timeInMinutes) {
            idleConnectCheckTimeInMinutes = timeInMinutes;
            return this;
        }

        public SshClientConfiguration build() {
            return new SshClientConfiguration(this);
        }
    }

    private SshClientConfiguration(Builder builder) {
        this.totalConnectAttempts = builder.totalConnectAttempts;
        this.connectWaitTime = builder.connectWaitTime;
        this.readTimeout = builder.readTimeout;
        this.readLatency = builder.readLatency;
        this.ptySupport = builder.ptySupport;
        this.bufferSize = builder.bufferSize;
        this.uniqueId = builder.uniqueId;
        this.localCacheAllowed = builder.localCacheAllowed;
        this.cachedFilesPath = builder.cachedFilesPath;
        this.shellType = builder.shellType;
        this.threadPoolConfiguration = builder.threadPoolConfiguration;
        this.idleConnectCheckTimeInMinutes = builder.idleConnectCheckTimeInMinutes;
    }

    public int getTotalConnectAttempts() {
        return totalConnectAttempts;
    }

    public int getConnectWaitTime() {
        return connectWaitTime;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getReadLatency() {
        return readLatency;
    }

    public boolean isPtySupport() {
        return ptySupport;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getCachedFilesPath() {
        return cachedFilesPath;
    }

    public boolean isLocalCacheAllowed() {
        return localCacheAllowed;
    }

    public ShellType getShellType() {
        return shellType;
    }

    public short getIdleConnectCheckTimeInMinutes() {
        return idleConnectCheckTimeInMinutes;
    }

    public ThreadPoolConfiguration getThreadPoolConfiguration() {
        return threadPoolConfiguration;
    }
}
