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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Unit test for ConditionalThreadBlocker
 */
public class ConditionalThreadBlockerTest {

	private void sleep(int timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testBlockSuccess() {
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(new ConditionConfiguration.Builder().build());
		BlockerThread<Integer> blockerThread = new BlockerThread<>(101, blocker);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		blockerThread.start();
		sleep(20);
		unBlockerThread.start();

		try {
			blockerThread.join();
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(blockerThread.isSuccess());
		assertTrue(unBlockerThread.isSuccess());
	}

	@Test
	public void testBlockWithTimeoutSuccess() {
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(new ConditionConfiguration.Builder().build());
		BlockerThreadWithTimeout<Integer> blockerThread = new BlockerThreadWithTimeout<>(101, blocker, 20);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		blockerThread.start();
		sleep(10);
		unBlockerThread.start();

		try {
			blockerThread.join();
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(blockerThread.isSuccess());
		assertTrue(unBlockerThread.isSuccess());
	}

	@Test
	public void testBlockTimedout() {
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(new ConditionConfiguration.Builder().build());
		BlockerThreadWithTimeout<Integer> blockerThreadWithTimeout = new BlockerThreadWithTimeout<>(101, blocker, 10);

		blockerThreadWithTimeout.start();

		try {
			blockerThreadWithTimeout.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertFalse(blockerThreadWithTimeout.isSuccess());
		Throwable t = blockerThreadWithTimeout.getThrowable();
		assertNotNull(t);
		assertTrue(t instanceof ConditionTimeoutException);
	}

	@Test
	public void testDelayedBlockSuccess() {
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(new ConditionConfiguration.Builder().build());
		BlockerThreadWithTimeout<Integer> blockerThread = new BlockerThreadWithTimeout<>(101, blocker, 10);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		unBlockerThread.start();
		sleep(20);
		blockerThread.start();

		try {
			blockerThread.join();
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(blockerThread.isSuccess());
		assertTrue(unBlockerThread.isSuccess());
	}

	@Test
	public void testEarlyReleaseEnabled() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		unBlockerThread.start();

		try {
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(unBlockerThread.isSuccess());

	}

	@Test
	public void testEarlyReleasedBasicSuccess() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		unBlockerThread.start();

		try {
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(unBlockerThread.isSuccess());
		assertEquals(1, blocker.earlyReleasedCount());

		BlockerThread<Integer> blockerThread = new BlockerThread<>(101, blocker);
		blockerThread.start();

		try {
			blockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(blockerThread.isSuccess());
		assertEquals(0, blocker.earlyReleasedCount());

	}

	@Test
	public void testEarlyReleasedWithLoadSuccessScenario1() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);

		List<UnBlockerThread<Integer>> unBlockerList = new ArrayList<>();
		int loadCount = 50;
		for (int i = 0; i < loadCount; i++) {
			UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101 + i, blocker);
			unBlockerList.add(unBlockerThread);
			unBlockerThread.start();
		}

		for (int i = 0; i < loadCount; i++) {
			try {
				unBlockerList.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		for (int i = 0; i < loadCount; i++) {
			assertTrue(unBlockerList.get(i).isSuccess());
		}
		assertEquals(loadCount, blocker.earlyReleasedCount());
	}

	@Test
	public void testEarlyReleasedWithLoadSuccessScenario2() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);

		List<UnBlockerThread<Integer>> unBlockerList = new ArrayList<>();
		int loadCount = 50;
		for (int i = 0; i < loadCount; i++) {
			UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101 + i, blocker);
			unBlockerList.add(unBlockerThread);
			unBlockerThread.start();
		}

		for (int i = 0; i < loadCount; i++) {
			try {
				unBlockerList.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		for (int i = 0; i < loadCount; i++) {
			assertTrue(unBlockerList.get(i).isSuccess());
		}
		assertEquals(loadCount, blocker.earlyReleasedCount());

		List<BlockerThread<Integer>> blockerList = new ArrayList<>();
		for (int i = 0; i < loadCount; i++) {
			BlockerThread<Integer> blockerThread = new BlockerThread<>(101 + i, blocker);
			blockerList.add(blockerThread);
			blockerThread.start();
		}

		for (int i = 0; i < loadCount; i++) {
			try {
				blockerList.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		for (int i = 0; i < loadCount; i++) {
			assertTrue(blockerList.get(i).isSuccess());
		}
		assertEquals(0, blocker.earlyReleasedCount());
	}

	@Test
	public void testEarlyReleaseDisabledSuccess() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().disableEarlyRelease(3, 5).build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);
		BlockerThreadWithTimeout<Integer> blockerThread = new BlockerThreadWithTimeout<>(101, blocker, 50);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		blockerThread.start();
		sleep(10);
		unBlockerThread.start();

		try {
			blockerThread.join();
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(blockerThread.isSuccess());
		assertTrue(unBlockerThread.isSuccess());
	}

	@Test
	public void testEarlyReleaseDisabledDelayedSuccess() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().disableEarlyRelease(10, 100).build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);
		BlockerThreadWithTimeout<Integer> blockerThread = new BlockerThreadWithTimeout<>(101, blocker, 150);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		unBlockerThread.start();
		sleep(10);
		blockerThread.start();

		try {
			blockerThread.join();
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		if (!blockerThread.isSuccess()) {
			Throwable t = blockerThread.getThrowable();
			if (t != null) {
				t.printStackTrace();
			}
		}

		if (!unBlockerThread.isSuccess()) {
			Throwable t = unBlockerThread.getThrowable();
			if (t != null) {
				t.printStackTrace();
			}
		}

		assertTrue(blockerThread.isSuccess());
		assertTrue(unBlockerThread.isSuccess());
	}

	@Test
	public void testEarlyReleaseDisabledFail() {
		ConditionConfiguration config = new ConditionConfiguration.Builder().disableEarlyRelease(3, 5).build();
		ConditionalThreadBlocker<Integer> blocker = new ConditionalThreadBlocker<>(config);
		UnBlockerThread<Integer> unBlockerThread = new UnBlockerThread<>(101, blocker);

		unBlockerThread.start();

		try {
			unBlockerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertFalse(unBlockerThread.isSuccess());
	}

	class BlockerThread<K> extends Thread {
		private K		                    id;
		private ConditionalThreadBlocker<K>	blocker;
		private boolean		                success;

		BlockerThread(K id, ConditionalThreadBlocker<K> blocker) {
			this.id = id;
			this.blocker = blocker;
		}

		public void run() {
			try {
				ExitStatus exitStatus = blocker.blockOnCondition(id);
				if (ExitStatus.CANCELLED == exitStatus) {
					throw new ConditionCancelledException("cancel() called for the condition : " + id);
				} else if (ExitStatus.TIMEOUT == exitStatus) {
					throw new ConditionTimeoutException("Timeout occurred for the condition : " + id);
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

	class BlockerThreadWithTimeout<K> extends Thread {
		private K		                    id;
		private ConditionalThreadBlocker<K>	blocker;
		private long		                timeoutInMillis;
		private boolean		                success;
		private Throwable		            throwable;

		BlockerThreadWithTimeout(K id, ConditionalThreadBlocker<K> blocker, long timeoutInMillis) {
			this.id = id;
			this.blocker = blocker;
			this.timeoutInMillis = timeoutInMillis;
		}

		public void run() {
			try {
				ExitStatus exitStatus = blocker.blockOnCondition(id, timeoutInMillis, TimeUnit.MILLISECONDS);

				if (ExitStatus.CANCELLED == exitStatus) {
					throw new ConditionCancelledException("cancel() called for the condition : " + id);
				} else if (ExitStatus.TIMEOUT == exitStatus) {
					throw new ConditionTimeoutException("Timeout occurred for the condition : " + id);
				}

				success = true;
			} catch (Throwable t) {
				throwable = t;
			}
		}

		public boolean isSuccess() {
			return success;
		}

		public Throwable getThrowable() {
			return throwable;
		}
	}

	class UnBlockerThread<K> extends Thread {
		private K		                    id;
		private ConditionalThreadBlocker<K>	blocker;
		private boolean		                success;
		private Throwable		            throwable;

		UnBlockerThread(K id, ConditionalThreadBlocker<K> blocker) {
			this.id = id;
			this.blocker = blocker;
		}

		public void run() {
			try {
				blocker.releaseForCondition(id);
				success = true;
			} catch (Throwable t) {
				throwable = t;
			}
		}

		public boolean isSuccess() {
			return success;
		}

		public Throwable getThrowable() {
			return throwable;
		}
	}
}
