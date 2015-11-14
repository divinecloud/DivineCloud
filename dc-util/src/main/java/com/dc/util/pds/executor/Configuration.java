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

package com.dc.util.pds.executor;

/**
 * Configuration class for PartitionQueue properties.
 */
public class Configuration {
    private final int minNoOfThreads;
    private final int maxNoOfThreads;
    private final int idleThreadCleanupInSecs;
    private final int expiryTimeInSecs;
    private final int messageBatchSize;
    private boolean implicitClose;
    private final int threadIncreaseThreshold;

    public static class Builder {
        private int minNoOfThreads = 5;
        private int maxNoOfThreads = 10;
        private int expiryTimeInSecs = 2;
        private int idleThreadCleanupInSecs = 60;
        private int messageBatchSize = 10;
        private boolean implicitClose;
        private int threadIncreaseThreshold = 25;

        public Builder minThreads(int val) {
            minNoOfThreads = val;
            return this;
        }

        public Builder maxThreads(int val) {
            maxNoOfThreads = val;
            return this;
        }

        public Builder expiryTimeInSecs(int val) {
            expiryTimeInSecs = val;
            return this;
        }

        public Builder messageBatchSize(int val) {
            messageBatchSize = val;
            return this;
        }

        public Builder daemonThreads(boolean val) {
            implicitClose = val;
            return this;
        }

        public Builder threadIncreaseThreshold(int val) {
            threadIncreaseThreshold = val;
            return this;
        }

        public Builder idleThreadCleanupInSecs(int val) {
            idleThreadCleanupInSecs = val;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }

    }

    private Configuration(Builder builder) {
        minNoOfThreads = builder.minNoOfThreads;
        maxNoOfThreads = builder.maxNoOfThreads;
        expiryTimeInSecs = builder.expiryTimeInSecs;
        messageBatchSize = builder.messageBatchSize;
        implicitClose = builder.implicitClose;
        threadIncreaseThreshold = builder.threadIncreaseThreshold;
        idleThreadCleanupInSecs = builder.idleThreadCleanupInSecs;
    }

    public int getMinNoOfThreads() {
        return minNoOfThreads;
    }

    public int getMaxNoOfThreads() {
        return maxNoOfThreads;
    }

    public int getExpiryTimeInSecs() {
        return expiryTimeInSecs;
    }

    public int getMessageBatchSize() {
        return messageBatchSize;
    }

    public boolean isImplicitClose() {
        return implicitClose;
    }

    public int getThreadIncreaseThreshold() {
        return threadIncreaseThreshold;
    }

    public int getIdleThreadCleanupInSecs() {
        return idleThreadCleanupInSecs;
    }
}
