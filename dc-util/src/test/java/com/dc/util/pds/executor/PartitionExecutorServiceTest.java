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

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.dc.util.pds.executor.ordered.Worker;
import com.dc.util.pds.executor.ordered.WorkerCreator;

/**
 * Unit test for Partition executor service.
 */
public class PartitionExecutorServiceTest {

	@Test
	public void testEmptyScenario() {
		testingEmptyScenario(true);
		testingEmptyScenario(false);
	}

	private void testingEmptyScenario(boolean ordered) {
		Configuration configuration = new Configuration.Builder().minThreads(2).maxThreads(2).messageBatchSize(10).build();
		AtomicInteger counter = new AtomicInteger();
		MessageHandler messageHandler = new MessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);

		try {
			executorService.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	public void testSuccessScenario() {
		testingSuccessScenario(true);
		testingSuccessScenario(false);
	}

	private void testingSuccessScenario(boolean ordered) {
		Configuration configuration = new Configuration.Builder().minThreads(2).maxThreads(2).messageBatchSize(10).build();
		AtomicInteger counter = new AtomicInteger();
		MessageHandler messageHandler = new MessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);
		for (int i = 0; i < 10; i++) {
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
	public void testSuccessScenarioWithMaxThreadTrigger() {
		testingSuccessScenarioWithMaxThreadTrigger(true);
		testingSuccessScenarioWithMaxThreadTrigger(false);
	}

	private void testingSuccessScenarioWithMaxThreadTrigger(boolean ordered) {
		Configuration configuration = new Configuration.Builder().minThreads(2).maxThreads(10).messageBatchSize(10).build();
		AtomicInteger counter = new AtomicInteger();
		MessageHandler messageHandler = new MessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);
		for (int i = 0; i < 10000; i++) {
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
			assertTrue(10000 == counter.get());
			assertTrue(10000 == messageHandler.statsMap.size());
			assertTrue(10 == messageHandler.threadIdMap.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSinglePartitionScenario() {
		testingSinglePartitionScenario(true);
		testingSinglePartitionScenario(false);
	}

	private void testingSinglePartitionScenario(boolean ordered) {
		int threadCount = 4;

		Configuration configuration = new Configuration.Builder().minThreads(threadCount).maxThreads(threadCount).messageBatchSize(10).build();
		AtomicInteger counter = new AtomicInteger();
		MessageHandler messageHandler = new MessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
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
			assertTrue(1000 == counter.get());
			assertTrue(1 == messageHandler.statsMap.size());
			int matchValue = (ordered) ? 1 : threadCount;
			assertTrue(matchValue == messageHandler.threadIdMap.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testTwoPartitionsScenario() {
		testingTwoPartitionsScenario(true);
		testingTwoPartitionsScenario(false);
	}

	private void testingTwoPartitionsScenario(boolean ordered) {
		int threadCount = 4;
		Configuration configuration = new Configuration.Builder().minThreads(threadCount).maxThreads(threadCount).messageBatchSize(10).build();
		AtomicInteger counter = new AtomicInteger();
		MessageHandler messageHandler = new MessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
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
			assertTrue(1000 == counter.get());
			assertTrue(2 == messageHandler.statsMap.size());
			int matchValue = (ordered) ? 2 : threadCount;
			assertTrue(matchValue == messageHandler.threadIdMap.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSuccessScenario2() {
		testingSuccessScenario2(true);
		testingSuccessScenario2(false);
	}

	private void testingSuccessScenario2(boolean ordered) {
		Configuration configuration = new Configuration.Builder().minThreads(5).maxThreads(20).messageBatchSize(10).build();

		AtomicInteger counter = new AtomicInteger();
		SlowMessageHandler messageHandler = new SlowMessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);
		for (int i = 0; i < 1000; i++) {
			String msg = "message_" + (i + 1);
			String partitionId = (i + 1) + "";
			try {
				executorService.submit(partitionId, msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		for (int i = 1000; i < 2000; i++) {
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
		testingSuccessScenario3(true);
		testingSuccessScenario3(false);
	}

	private void testingSuccessScenario3(boolean ordered) {
		Configuration configuration = new Configuration.Builder().minThreads(5).maxThreads(20).messageBatchSize(10).build();
		AtomicInteger counter = new AtomicInteger();
		SlowMessageHandler messageHandler = new SlowMessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);
		for (int i = 0; i < 10; i++) {
			try {
				String partitionId = (i + 1) + "";
				for (int j = 0; j < 100; j++) {
					String msg = "message_" + (i + 1) + "_" + (j + 1);
					executorService.submit(partitionId, msg);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		for (int i = 0; i < 100; i++) {
			try {
				String partitionId = (i + 1) + "";
				for (int j = 0; j < 100; j++) {
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
		testingConcurrentAccessScenario(true);
		testingConcurrentAccessScenario(false);
	}

	private void testingConcurrentAccessScenario(boolean ordered) {
		Configuration configuration = new Configuration.Builder().minThreads(20).maxThreads(20).messageBatchSize(10).build();

		AtomicInteger counter = new AtomicInteger();
		MessageHandler messageHandler = new MessageHandler(counter);
		PartitionExecutorService<String> executorService = new PartitionExecutorServiceBuilder<String>().build(messageHandler, configuration, ordered);
		WorkerCreator workerCreator = new WorkerCreator(executorService);
		List<Worker> workersList = workerCreator.createWorkers(20, 1000, 0);

		long startTime = System.currentTimeMillis();
		for (Worker worker : workersList) {
			worker.start();
		}

		for (Worker worker : workersList) {
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
		if (result) {
			for (String aSet : set) {
				boolean matches = inputStatsMap.get(aSet).get() == statsMap.get(aSet).get();
				if (!matches) {
					return false;
				}
			}
		}
		return result;
	}

}
