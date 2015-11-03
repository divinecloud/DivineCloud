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

package com.dc.util.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BatchExecutorService {
    private int batchSize;
    private List<BatchUnitTask> tasksList;
    private AtomicReference<List<WorkerThread>> workerThreadReference;
    private volatile boolean cancelled;

    public BatchExecutorService(int batchSize, List<BatchUnitTask> tasksList) {
        this.batchSize = batchSize;
        this.tasksList = tasksList;
        workerThreadReference = new AtomicReference<>();
    }

    public void cancel() {
        cancelled = true;
    }

    public void execute() throws InterruptedException {
        int batchCount = tasksList.size()  / batchSize;
        int index = 0;
        for(int i = 0; i < batchCount; i++) {
            if(!cancelled) {
                //System.out.println("Batch Set : " + (i + 1) + " Start - " + index + " End - " + (index + batchSize));
                index = executeInBatch(index, index + batchSize);
            }
        }
        int remainingCount = tasksList.size() - (batchCount * batchSize);
        if(tasksList.size() > (batchCount * batchSize)) {
            if(!cancelled) {
                //System.out.println("Batch Set : LAST " + " Start - " + index + " End - " + (index + remainingCount));
                executeInBatch(index, index + remainingCount);
            }
        }

    }

    private int executeInBatch(int startIndex, int endIndex) throws InterruptedException {
        List<WorkerThread> list = new ArrayList<>();
        workerThreadReference.set(list);
        for(int i = startIndex; i<endIndex; i++) {
            if(!cancelled) {
                WorkerThread worker = new WorkerThread(i);
                list.add(worker);
                worker.start();
            }
        }

        if(!cancelled) {
            for (WorkerThread aList : list) {
                aList.join();
            }
        }
        workerThreadReference.set(null);
        return endIndex;
    }


    private class WorkerThread extends Thread {
        private int index;

        public WorkerThread(int index) {
            this.index = index;
        }

        public void run() {
            tasksList.get(index).execute();
        }
    }
}
