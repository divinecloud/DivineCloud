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
 * Unit test for ConditionalBarrier class.
 */
public class ConditionalBarrierTest {

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
		ConditionalBarrier<Integer> barrier = new ConditionalBarrier<>();
		BlockerThread<Integer> blocker = new BlockerThread<>(101, barrier);
		UnBlockerThread<Integer> unBlocker = new UnBlockerThread<>(101, barrier);

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
		ConditionalBarrier<Integer> barrier = new ConditionalBarrier<>();
		BlockerThreadWithTimeout<Integer> blocker = new BlockerThreadWithTimeout<>(101, barrier, 100);
		UnBlockerThread<Integer> unBlocker = new UnBlockerThread<>(101, barrier);

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
		ConditionalBarrier<Integer> barrier = new ConditionalBarrier<>();
		BlockerThreadWithTimeout<Integer> blocker = new BlockerThreadWithTimeout<>(101, barrier, 10);

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
        ConditionalBarrier<Integer> barrier = new ConditionalBarrier<>();
        barrier.release(100);
        barrier.release(101);
        barrier.release(102);
        barrier.release(103);
        sleep(10);
        int purgedItems = barrier.purge(System.currentTimeMillis());
        assertEquals(4, purgedItems);
    }

	class BlockerThread<K> extends Thread {
		private K		              id;
		private ConditionalBarrier<K>	barrier;
		private boolean		          success;

		BlockerThread(K id, ConditionalBarrier<K> barrier) {
			this.id = id;
			this.barrier = barrier;
		}

		public void run() {
			try {
				barrier.block(id);
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
		private K		              id;
		private ConditionalBarrier<K>	barrier;
		private long		          timeoutInMillis;
		private boolean		          success;
		private Throwable		      throwable;

		BlockerThreadWithTimeout(K id, ConditionalBarrier<K> barrier, long timeoutInMillis) {
			this.id = id;
			this.barrier = barrier;
			this.timeoutInMillis = timeoutInMillis;
		}

		public void run() {
			try {
				barrier.block(id, timeoutInMillis, TimeUnit.MILLISECONDS);
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
		private K		              id;
		private ConditionalBarrier<K>	barrier;
		private boolean		          success;

		UnBlockerThread(K id, ConditionalBarrier<K> barrier) {
			this.id = id;
			this.barrier = barrier;
		}

		public void run() {
			try {
				barrier.release(id);
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
