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

package com.dc.util.condition;

/**
 * Configuration details for ConditionalBlocker.
 */
public class ConditionConfiguration {

    private int retryAttempts;
    private long sleepTimePerAttempt;
    private boolean earlyReleaseDisabled;

    public static class Builder {
        private int retryAttempts;
        private long sleepTimePerAttempt;
        private boolean earlyReleaseDisabled;

        public Builder disableEarlyRelease(int retryAttempts, long sleepTimePerAttempt) {
            earlyReleaseDisabled = true;
            this.retryAttempts = retryAttempts;
            this.sleepTimePerAttempt = sleepTimePerAttempt;
            if(retryAttempts <= 0 || retryAttempts > 20) {
                throw new RuntimeException("Retry attempt has to be greater than 0 and less than or equal to 20");
            }
            if(sleepTimePerAttempt <=0 || sleepTimePerAttempt > 1000) {
                throw new RuntimeException("sleepTimePerAttempt has to be greater than 0 and less than or equal to 1000");
            }
            return this;
        }

        public ConditionConfiguration build() {
            return new ConditionConfiguration(this);
        }
    }

    private ConditionConfiguration(Builder builder) {
        this.retryAttempts = builder.retryAttempts;
        this.sleepTimePerAttempt = builder.sleepTimePerAttempt;
        this.earlyReleaseDisabled = builder.earlyReleaseDisabled;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public long getSleepTimePerAttempt() {
        return sleepTimePerAttempt;
    }

    public boolean isEarlyReleaseDisabled() {
        return earlyReleaseDisabled;
    }
}
