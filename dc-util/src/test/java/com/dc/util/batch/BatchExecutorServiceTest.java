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

package com.dc.util.batch;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class BatchExecutorServiceTest {

    @Test
    public void testExecute() {
        int batchSize = 10;
        AtomicInteger sum = new AtomicInteger();
        AtomicInteger counter = new AtomicInteger();
        List<BatchUnitTask> list = new ArrayList<>();
        int expectedSum = 0;
        int expectedCounter = 0;
        for(int i=0; i<24; i++) {
            BatchUnitTask command = new BatchUnitTaskSample(sum, counter, i);
            expectedSum+=i;
            expectedCounter++;
            list.add(command);
        }
        BatchExecutorService service = new BatchExecutorService(batchSize, list);
        try {
            long startTime = System.nanoTime();
            service.execute();
            long endTime = System.nanoTime();
            System.out.println(endTime - startTime);
            assertEquals(expectedCounter, counter.get());
            assertEquals(expectedSum, sum.get());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class BatchUnitTaskSample implements BatchUnitTask {
        private AtomicInteger sum;
        private AtomicInteger counter;
        private int number;

        public BatchUnitTaskSample(AtomicInteger sum, AtomicInteger counter, int number) {
            this.sum = sum;
            this.counter = counter;
            this.number = number;
        }

        @Override
        public void execute() {
            counter.incrementAndGet();
            sum.addAndGet(number);
            System.out.println("Index : " + number);
        }
    }
}

