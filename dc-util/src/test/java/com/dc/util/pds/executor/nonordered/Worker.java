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

package com.dc.util.pds.executor.nonordered;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.util.pds.executor.PartitionExecutorService;

public class Worker extends Thread {
    private String id;
    private int workUnit;
    private int delayInMillis;
    private PartitionExecutorService<String> service;
    private Map<String, AtomicInteger> inputStatsMap;
    Worker(String id, int workUnit, PartitionExecutorService<String> service, int delayInMillis, Map<String, AtomicInteger> inputStatsMap) {
        this.id = id;
        this.workUnit = workUnit;
        this.service = service;
        this.delayInMillis = delayInMillis;
        this.inputStatsMap = inputStatsMap;
    }

    public void run() {
        AtomicInteger counter = new AtomicInteger();
        inputStatsMap.put("partition" + id, counter);

        for(int i=0; i<workUnit; i++) {
            try {
                service.submit("partition" + id, "message" + id);
                counter.incrementAndGet();
                if(delayInMillis > 0) {
                    Thread.sleep(delayInMillis);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace(); //@TODO: Add appropriate logic here
            }
        }
    }
}