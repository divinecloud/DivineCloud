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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.util.pds.executor.PartitionExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: nepuhb
 * Date: Apr 15, 2012
 * Time: 2:44:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkerCreator {
    private PartitionExecutorService<String> service;
    public Map<String, AtomicInteger> inputStatsMap;

    public WorkerCreator(PartitionExecutorService<String> service) {
        this.service = service;
        inputStatsMap = new ConcurrentHashMap<String, AtomicInteger>();
    }

    public List<Worker> createWorkers(int workerCount, int workUnit, int delayInMillis) {
        List<Worker> workersList = new ArrayList<Worker>(workerCount);
        for(int i=0; i<workerCount; i++) {
            workersList.add(new Worker((i+ 1) +"", workUnit, service, delayInMillis, inputStatsMap));
        }
        return workersList;
    }
}