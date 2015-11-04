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

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates the unique command execution id.
 */
public class CommandExecIdGenerator {
    private final AtomicInteger counter;
    private String uniqueId;

    public static final char SEPARATOR = '/';
    private static final String OUTPUT_FILE_PREFIX = "DC";
    private static final int UPPER_THRESHOLD = 99999999;

    public CommandExecIdGenerator(String uniqueId) {
        this.uniqueId = uniqueId;
        counter = new AtomicInteger(1);
    }

    public String generate() {
        if(counter.get() > UPPER_THRESHOLD) {
            synchronized (counter) {
                counter.set(1);
            }
        }
        Calendar today = Calendar.getInstance();
        return new StringBuilder().append(today.get(Calendar.YEAR)).append(today.get(Calendar.MONTH)).append(SEPARATOR)
                .append(today.get(Calendar.DAY_OF_MONTH)).append(SEPARATOR)
                .append(today.get(Calendar.HOUR)).append(uniqueId).append(SEPARATOR).append(OUTPUT_FILE_PREFIX)
                .append(today.get(Calendar.MINUTE)).append(today.get(Calendar.SECOND))
                .append(OUTPUT_FILE_PREFIX).append(counter.incrementAndGet()).toString();
    }
}
