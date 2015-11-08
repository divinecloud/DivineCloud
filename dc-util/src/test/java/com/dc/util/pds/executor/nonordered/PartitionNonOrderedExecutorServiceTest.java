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

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.dc.util.pds.executor.Configuration;
import com.dc.util.pds.executor.MessageHandler;
import com.dc.util.pds.executor.SlowMessageHandler;

/**
 * Unit Test for Partition Non-Order Executor Service
 */
public class PartitionNonOrderedExecutorServiceTest {

    @Test
    public void testEmptyScenario() {
        Configuration configuration = new Configuration.Builder().minThreads(2).maxThreads(2).messageBatchSize(10).build();
        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        try {
            executorService.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testSuccessScenario() {
        Configuration configuration = new Configuration.Builder().minThreads(2).maxThreads(2).messageBatchSize(10).build();
        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        for(int i=0; i<10; i++) {
            String msg = "message_" + (i + 1);
            String partitionId = (i + 1) + "";
            try {
                executorService.submit(partitionId, msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            assertTrue(10 == counter.get());
            assertTrue(10 == messageHandler.statsMap.size());
            assertTrue(2 == messageHandler.threadIdMap.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public void testSinglePartitionScenario() {
        Configuration configuration = new Configuration.Builder().minThreads(4).maxThreads(4).messageBatchSize(1).build();
        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        long startTime = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            String msg = "message_" + (i + 1);
            String partitionId = "Partition_1";
            try {
                executorService.submit(partitionId, msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            assertTrue(10000 == counter.get());
            assertTrue(1 == messageHandler.statsMap.size());
            assertTrue(4 == messageHandler.threadIdMap.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTwoPartitionsScenario() {
        Configuration configuration = new Configuration.Builder().minThreads(4).maxThreads(4).messageBatchSize(10).build();
        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        long startTime = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            String msg = "message_" + (i + 1);
            String partitionId = "Partition_" + (i % 2);
            try {
                executorService.submit(partitionId, msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            assertTrue(10000 == counter.get());
            assertTrue(2 == messageHandler.statsMap.size());
            assertTrue(2 == messageHandler.threadIdMap.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testSuccessScenario2() {
        Configuration configuration = new Configuration.Builder().minThreads(5).maxThreads(20).messageBatchSize(10).build();

        AtomicInteger counter  = new AtomicInteger();
        SlowMessageHandler messageHandler = new SlowMessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        for(int i=0; i<1000; i++) {
            String msg = "message_" + (i + 1);
            String partitionId = (i + 1) + "";
            try {
                executorService.submit(partitionId, msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        for(int i=1000; i<2000; i++) {
            String msg = "message_" + (i + 1);
            String partitionId = (i + 1) + "";
            try {
                executorService.submit(partitionId, msg);
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }


        try {
            executorService.close();
            assertTrue(5 < messageHandler.threadIdMap.size() && messageHandler.threadIdMap.size() <= 20);
            assertTrue(2000 == counter.get());
            assertTrue(2000 == messageHandler.statsMap.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testSuccessScenario3() {
        Configuration configuration = new Configuration.Builder().minThreads(5).maxThreads(20).messageBatchSize(10).build();
        AtomicInteger counter  = new AtomicInteger();
        SlowMessageHandler messageHandler = new SlowMessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        for(int i=0; i<10; i++) {
            try {
                String partitionId = (i + 1) + "";
                for(int j=0; j<100; j++) {
                    String msg = "message_" + (i + 1) + "_" + (j + 1);
                    executorService.submit(partitionId, msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        for(int i=0; i<100; i++) {
            try {
                String partitionId = (i + 1) + "";
                for(int j=0; j<100; j++) {
                    String msg = "message_" + (i + 1) + "_" + (j + 1);
                    executorService.submit(partitionId, msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.submit("abc", "abc_msg");
            executorService.close();
            assertTrue(11001 == counter.get());
            assertTrue(101 == messageHandler.statsMap.size());
            assertTrue(5 < messageHandler.threadIdMap.size() && messageHandler.threadIdMap.size() <= 20);


        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testConcurrentAccessScenario() {
        Configuration configuration = new Configuration.Builder().minThreads(20).maxThreads(20).messageBatchSize(10).build();

        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        WorkerCreator workerCreator = new WorkerCreator(executorService);
        List<Worker> workersList = workerCreator.createWorkers(20, 1000, 0);

        long startTime = System.currentTimeMillis();
        for(Worker worker : workersList) {
            worker.start();
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));

            assertTrue(20000 == counter.get());

            Map<String, AtomicInteger> inputStatsMap = workerCreator.inputStatsMap;
            Map<String, AtomicInteger> statsMap = messageHandler.statsMap;
            assertTrue(20 == statsMap.size());
            assertTrue(20 == inputStatsMap.size());
            assertTrue(compare(inputStatsMap, statsMap));
            assertTrue(20 == messageHandler.threadIdMap.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private boolean compare(Map<String, AtomicInteger> inputStatsMap, Map<String, AtomicInteger> statsMap) {
        Set<String> inputSet = inputStatsMap.keySet();
        Set<String> set = statsMap.keySet();

        boolean result = inputSet.containsAll(set) && set.containsAll(inputSet);
        if(result) {
            for (String aSet : set) {
                boolean matches = inputStatsMap.get(aSet).get() == statsMap.get(aSet).get();
                if (!matches) {
                    return false;
                }
            }
        }
        return result;
    }

    @Test
    public void testSmallLoadScenario1() {
        Configuration configuration = new Configuration.Builder().minThreads(2).maxThreads(50).messageBatchSize(10).build();

        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        long startTime = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                String partitionId = (i + 1) + "";
                for(int j=0; j<100; j++) {
                    String msg = "message_" + (i + 1) + "_" + (j + 1);
                    executorService.submit(partitionId, msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            System.out.println(counter.get());
            assertTrue(10000 == counter.get());
            assertTrue(100 == messageHandler.statsMap.size());
            assertTrue(2 < messageHandler.threadIdMap.size() && messageHandler.threadIdMap.size() <= 50);


        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testSmallLoadScenario2() {
        Configuration configuration = new Configuration.Builder().minThreads(5).maxThreads(50).messageBatchSize(10).build();

        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        long startTime = System.currentTimeMillis();
        for(int i=0; i<100; i++) {
            try {
                String partitionId = (i + 1) + "";
                for(int j=0; j<100; j++) {
                    String msg = "message_" + (i + 1) + "_" + (j + 1);
                    executorService.submit(partitionId, msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            System.out.println(counter.get());
            assertTrue(10000 == counter.get());
            assertTrue(5 < messageHandler.threadIdMap.size() && messageHandler.threadIdMap.size() <= 50);


        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testMediumLoadScenario1() {
        Configuration configuration = new Configuration.Builder().minThreads(100).maxThreads(200).messageBatchSize(10).build();

        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        long startTime = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            try {
                String partitionId = (i + 1) + "";
                for(int j=0; j<100; j++) {
                    String msg = "message_" + (i + 1) + "_" + (j + 1);
                    executorService.submit(partitionId, msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            System.out.println(counter.get());
            assertTrue(1000000 == counter.get());
            assertTrue(100 < messageHandler.threadIdMap.size() && messageHandler.threadIdMap.size() <= 200);

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testLargeLoadScenario1() {
        Configuration configuration = new Configuration.Builder().minThreads(200).maxThreads(250).messageBatchSize(10).build();

        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        long startTime = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            try {
                String partitionId = (i + 1) + "";
                for(int j=0; j<1000; j++) {
                    String msg = "message_" + (i + 1) + "_" + (j + 1);
                    executorService.submit(partitionId, msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }

        try {
            executorService.close();
            long endTime = System.currentTimeMillis();
            System.out.println("Total Time : " + (endTime - startTime));
            System.out.println(counter.get());
            assertTrue(10000000 == counter.get());
            assertTrue(200 < messageHandler.threadIdMap.size() && messageHandler.threadIdMap.size() <= 250);


        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testCloseNow() {
        Configuration configuration = new Configuration.Builder().minThreads(2).maxThreads(2).messageBatchSize(10).build();

        AtomicInteger counter  = new AtomicInteger();
        MessageHandler messageHandler = new MessageHandler(counter);
        PartitionNonOrderedExecutorService<String> executorService = new PartitionNonOrderedExecutorService<String>(messageHandler, configuration);
        for(int i=0; i<10; i++) {
            String msg = "message_" + (i + 1);
            String partitionId = (i + 1) + "";
            try {
                executorService.submit(partitionId, msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }


        executorService.closeNow();
        assertTrue(10 >= counter.get());
    }

}

