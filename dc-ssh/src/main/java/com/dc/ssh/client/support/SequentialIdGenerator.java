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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique sequential ID.
 */
public class SequentialIdGenerator {

    private AtomicInteger counter;
    private int           threshold;
    private int           startCount;

    public SequentialIdGenerator(int startCount) {

        this.startCount = startCount;
        counter = new AtomicInteger(startCount);
        threshold = Integer.MAX_VALUE - 10000;
    }

    public int generate() {
        int id = counter.incrementAndGet();
        if (id >= threshold) {
            synchronized (this) {
                if (counter.get() >= threshold) {
                    counter = new AtomicInteger(startCount);
                }
                id = counter.incrementAndGet();
            }
        }
        return id;
    }
}
