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

import com.dc.util.condition.exception.ConditionTimeoutException;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Unit test for ConditionalResultBarrier class.
 */
public class ConditionalResultBarrierTest {

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
		ConditionalResultBarrier<Integer, String> barrier = new ConditionalResultBarrier<>();
		BlockerThread<Integer, String> blocker = new BlockerThread<>(101, "Result", barrier);
		UnBlockerThread<Integer, String> unBlocker = new UnBlockerThread<>(101, "Result", barrier);

		blocker.start();
		sleep(20);
		unBlocker.start();

		try {
			blocker.join();
			unBlocker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(blocker.isSuccess());
		assertTrue(unBlocker.isSuccess());
	}

	@Test
	public void testBlockWithTimeoutSuccess() {
		ConditionalResultBarrier<Integer, String> barrier = new ConditionalResultBarrier<>();
		BlockerThreadWithTimeout<Integer, String> blocker = new BlockerThreadWithTimeout<>(101, "Result", barrier, 100);
		UnBlockerThread<Integer, String> unBlocker = new UnBlockerThread<>(101, "Result", barrier);

		blocker.start();
		sleep(20);
		unBlocker.start();

		try {
			blocker.join();
			unBlocker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(blocker.isSuccess());
		assertTrue(unBlocker.isSuccess());
	}

	@Test
	public void testBlockTimedout() {
		ConditionalResultBarrier<Integer, String> barrier = new ConditionalResultBarrier<>();
		BlockerThreadWithTimeout<Integer, String> blocker = new BlockerThreadWithTimeout<>(101, "Result", barrier, 10);

		blocker.start();

		try {
			blocker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertFalse(blocker.isSuccess());
		Throwable t = blocker.getThrowable();
		assertNotNull(t);
		assertTrue(t instanceof ConditionTimeoutException);

	}

    @Test
    public void testPurge() {
        ConditionalResultBarrier<Integer, String> barrier = new ConditionalResultBarrier<>();
        barrier.release(100, "Result");
        barrier.release(101, "Result");
        barrier.release(102, "Result");
        barrier.release(103, "Result");
        sleep(10);
        int purgedItems = barrier.purge(System.currentTimeMillis());
        assertEquals(4, purgedItems);
    }

	class BlockerThread<I, K> extends Thread {
		private I		              id;
		private K		              result;
		private ConditionalResultBarrier<I, K>	barrier;
		private boolean		          success;

		BlockerThread(I id, K result, ConditionalResultBarrier<I, K> barrier) {
			this.id = id;
			this.result = result;
			this.barrier = barrier;
		}

		public void run() {
			try {
				K unblockResult = barrier.block(id);
				success = (unblockResult.equals(result));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		public boolean isSuccess() {
			return success;
		}
	}

	class BlockerThreadWithTimeout<I, K> extends Thread {
		private I		              id;
		private K		              result;
		private ConditionalResultBarrier<I, K>	barrier;
		private long		          timeoutInMillis;
		private boolean		          success;
		private Throwable		      throwable;

		BlockerThreadWithTimeout(I id, K result, ConditionalResultBarrier<I, K> barrier, long timeoutInMillis) {
			this.id = id;
			this.result = result;
			this.barrier = barrier;
			this.timeoutInMillis = timeoutInMillis;
		}

		public void run() {
			try {
				K unblockResult = barrier.block(id, timeoutInMillis, TimeUnit.MILLISECONDS);
				success = unblockResult.equals(result);
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

	class UnBlockerThread<I, K> extends Thread {
		private I		              id;
		private K		              result;
		private ConditionalResultBarrier<I, K>	barrier;
		private boolean		          success;

		UnBlockerThread(I id, K result, ConditionalResultBarrier<I, K> barrier) {
			this.id = id;
			this.result = result;
			this.barrier = barrier;
		}

		public void run() {
			try {
				barrier.release(id, result);
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
