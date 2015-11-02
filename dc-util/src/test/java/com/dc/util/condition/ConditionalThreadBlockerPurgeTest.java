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

import com.dc.util.condition.exception.ConditionCancelledException;
import com.dc.util.condition.exception.ConditionTimeoutException;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Unit test for ConditionalThreadBlocker purge functionality.
 */
public class ConditionalThreadBlockerPurgeTest {

	private void sleep(int timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testPurgeBasicSuccess() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);
		int count = 10;
		UnBlockerThread unBlockerThread = new UnBlockerThread(101, count, blocker);

		unBlockerThread.start();

		try {
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		long time1 = System.currentTimeMillis();

		assertTrue(unBlockerThread.isSuccess());
		assertEquals(count, blocker.earlyReleasedCount());
		sleep(5);
		int purgedCount = blocker.purge(time1);
		assertEquals(purgedCount, count);
		assertEquals(0, blocker.earlyReleasedCount());
	}

	@Test
	public void testPurgeWithLoadSuccess() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);
		int count = 50;
		UnBlockerThread unBlockerThread = new UnBlockerThread(101, count, blocker);

		unBlockerThread.start();

		try {
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		long time1 = System.currentTimeMillis();

		assertTrue(unBlockerThread.isSuccess());
		assertEquals(count, blocker.earlyReleasedCount());
		sleep(5);

		UnBlockerThread unBlockerThread2 = new UnBlockerThread(101 + count, 1, blocker);

		unBlockerThread2.start();

		try {
			unBlockerThread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertEquals(count + 1, blocker.earlyReleasedCount());

		int purgedCount = blocker.purge(time1);
		assertEquals(count, purgedCount);
		assertEquals(1, blocker.earlyReleasedCount());

	}

	@Test
	public void testPurgeConcurrentOperation() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);

		// 1. Load the early release map with enough data first
		int count = 300000;
		UnBlockerThread unBlockerThread = new UnBlockerThread(10000001, count, blocker);

		unBlockerThread.start();

		try {
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		long time1 = System.currentTimeMillis();

		assertTrue(unBlockerThread.isSuccess());
		assertEquals(count, blocker.earlyReleasedCount());

		sleep(5);

		// 2. After initial data set is loaded, start new thread to keep loading new data set for creating concurrency situation for purge
		int count2 = 300000;
		UnBlockerThread unBlockerThread2 = new UnBlockerThread(20000001, count2, blocker);

		unBlockerThread2.start();

		// 3. Run the blocker thread to add one more concurrency situation by checking the earlyReleaseMap before blocking
		BlockerThreadWithTimeout blockerThread = new BlockerThreadWithTimeout(30000001, 7500, blocker, 1);
		blockerThread.start();
		sleep(5);

		// 4. Start purging now
		int purgedCount = blocker.purge(time1);
		assertEquals(count, purgedCount);

		for (int i = 0; i < 100; i++) {
			int morePurgedCount = blocker.purge(time1);
			assertEquals(0, morePurgedCount);
		}

		try {
			unBlockerThread2.join();
			blockerThread.join();
			// assertTrue(blockerThread.isSuccess());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	class BlockerThreadWithTimeout extends Thread {
		private Integer		                      conditionId;
		private int		                          count;
		private ConditionalThreadBlocker<Integer>	blocker;
		private long		                      timeoutInMillis;
		private boolean		                      success;

		BlockerThreadWithTimeout(Integer conditionId, int count, ConditionalThreadBlocker<Integer> blocker, long timeoutInMillis) {
			this.conditionId = conditionId;
			this.count = count;
			this.blocker = blocker;
			this.timeoutInMillis = timeoutInMillis;
		}

		public void run() {
			for (int i = 0; i < count; i++) {
				try {
					int id = conditionId + i;
					ExitStatus exitStatus = blocker.blockOnCondition(id, timeoutInMillis, TimeUnit.MILLISECONDS);

					if (ExitStatus.CANCELLED == exitStatus) {
						throw new ConditionCancelledException("cancel() called for the condition : " + id);
					} else if (ExitStatus.TIMEOUT == exitStatus) {
						throw new ConditionTimeoutException("Timeout occurred for the condition : " + id);
					}

					success = true;
				} catch (Throwable t) {
					// t.printStackTrace();
					// Ignored for this particular scenario
				}
			}
		}

		public boolean isSuccess() {
			return success;
		}

	}

	class UnBlockerThread extends Thread {
		private Integer		                      conditionId;
		private int		                          count;
		private ConditionalThreadBlocker<Integer>	blocker;
		private boolean		                      success;

		UnBlockerThread(Integer conditionId, int count, ConditionalThreadBlocker<Integer> blocker) {
			this.conditionId = conditionId;
			this.blocker = blocker;
			this.count = count;
		}

		public void run() {
			try {
				for (int i = 0; i < count; i++) {
					blocker.releaseForCondition(conditionId + i);
				}
				success = true;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		public boolean isSuccess() {
			return success;
		}
	}

}
