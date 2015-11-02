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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.dc.util.condition.exception.ConcurrentConditionException;
import com.dc.util.condition.exception.ConditionException;
import com.dc.util.condition.exception.ConditionInputException;
import com.dc.util.condition.exception.ConditionInterruptedException;
import com.dc.util.condition.exception.ConditionNotExistsException;

/**
 * Blocks the thread until specific condition for the same Condition ID is signalled.
 */
public class ConditionalThreadBlocker<K> {

    private ReentrantLock lock;
    private final Map<K, Condition> conditionMap;
    private final Map<K, Boolean> cancellationSignalMap;
    private final Map<K, Long> earlyReleasedMap;

    private ConditionConfiguration configuration;

    private final static Boolean TRUE = true;

    private final static String NULL_ID_MESSAGE = "Null ID provided as input argument";
    private final static String DIFF_THREAD_CONDITION_BLOCKED_MESSAGE = "Different thread is already blocked on a given condition id : ";
    private final static String THREAD_INTERRUPTED_MESSAGE = "Thread Interrupted for condition id : ";
    private final static String NO_THREAD_BLOCKED_MESSAGE = "No Thread currently blocked for given condition id : ";


    public ConditionalThreadBlocker(ConditionConfiguration configuration) {
        this.configuration = configuration;
        lock = new ReentrantLock();
        conditionMap = new ConcurrentHashMap<>();
        cancellationSignalMap = new ConcurrentHashMap<>();
        earlyReleasedMap = new ConcurrentHashMap<>();
    }

    /**
     * This method should be called by the client code, once in a while to purge the elements that were early released, but
     * were never blocked for that condition. This situation should not arise unless there is a bug in usage of the API by
     * client code.
     *
     * @param oldTimeInMillis - how far to go in past from the current time, to purge the elements.
     * @return total elements purged
     */
    public int purge(long oldTimeInMillis) {
        int purgedCount = 0;
        synchronized (earlyReleasedMap) {
            List<K> toPurgeList = new ArrayList<>();
            Set<Map.Entry<K, Long>> entriesSet = earlyReleasedMap.entrySet();

            for (Map.Entry<K, Long> entry : entriesSet) {
                if (entry.getValue() <= oldTimeInMillis) {
                    toPurgeList.add(entry.getKey());
                }
            }

            if (toPurgeList.size() > 0) {
                for (K id : toPurgeList) {
                    Long purgedId = earlyReleasedMap.remove(id);
                    if (purgedId != null) {
                        purgedCount++;
                    }
                }
            }
        }

        return purgedCount;
    }

    public int earlyReleasedCount() {
        return earlyReleasedMap.size();
    }

    public ExitStatus blockOnCondition(K id) throws ConditionException {
        ExitStatus result = ExitStatus.DONE;

        lock.lock();
        try {

            Condition condition = createCondition(id);

            try {
                Long entryTime = earlyReleasedMap.get(id);
                if (entryTime != null) {
                    earlyReleasedMap.remove(id);
                } else {
                    condition.await();
                }
            } catch (InterruptedException e) {
                conditionMap.remove(id);
                throw new ConditionInterruptedException(THREAD_INTERRUPTED_MESSAGE + id, e);
            }
        }
        finally {
            lock.unlock();
        }
            Boolean cancelled = cancellationSignalMap.remove(id);
            if (cancelled != null) {
                result = ExitStatus.CANCELLED;
            }

        return result;
    }

    public ExitStatus blockOnCondition(K id, long timeout, TimeUnit timeUnit) throws ConditionException {
        ExitStatus result = ExitStatus.DONE;
        boolean success;

        lock.lock();
        try {
            Condition condition = createCondition(id);
            try {
                Long entryTime = earlyReleasedMap.get(id);
                if (entryTime != null) {
                    earlyReleasedMap.remove(id);
                    success = true;
                } else {
                    success = condition.await(timeout, timeUnit);
                }
                Boolean cancelled = false;
                Object cancelledObj = cancellationSignalMap.remove(id);
                if (cancelledObj != null) {
                    cancelled = (Boolean) cancelledObj;
                }
                if (!success || cancelled) {
                    conditionMap.remove(id);
                    if (cancelled) {
                        result = ExitStatus.CANCELLED;
                    } else {
                        result = ExitStatus.TIMEOUT;
                    }
                }
            } catch (InterruptedException e) {
                conditionMap.remove(id);
                throw new ConditionInterruptedException(THREAD_INTERRUPTED_MESSAGE + id, e);
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void cancelForCondition(K id) throws ConditionException {
        if (id == null) {
            throw new ConditionInputException(NULL_ID_MESSAGE);
        }

        cancellationSignalMap.put(id, TRUE);
        releaseForCondition(id);
    }

    public void releaseForCondition(K id) throws ConditionException {

            if (id == null) {
                throw new ConditionInputException(NULL_ID_MESSAGE);
            }

            int attempt = configuration.getRetryAttempts();
            long sleepTime = configuration.getSleepTimePerAttempt();
            boolean success = false;

            if (configuration.isEarlyReleaseDisabled()) {
                success = releaseConditionWithoutEarlyRelease(id, attempt, sleepTime, success);
                if (!success) {
                    throw new ConditionNotExistsException(NO_THREAD_BLOCKED_MESSAGE + id);
                }
            } else {
                releaseConditionWithEarlyRelease(id);
            }
    }

    private void releaseConditionWithEarlyRelease(K id) {
        boolean success;
        lock.lock();
        try {

            success = tryReleaseForCondition(id);

            if (!success) {
                    earlyReleasedMap.put(id, System.currentTimeMillis());
            }
        }
        finally {
            lock.unlock();
        }
    }

    private boolean releaseConditionWithoutEarlyRelease(K id, int attempt, long sleepTime, boolean success) throws ConditionInterruptedException {
        while (!success && attempt > 0) {
            lock.lock();
            try {
                success = tryReleaseForCondition(id);
            }
            finally {
                lock.unlock();
            }

            attempt = sleep(id, attempt, sleepTime, success);

        }
        return success;
    }

    private int sleep(K id, int attempt, long sleepTime, boolean success) throws ConditionInterruptedException {
        if (!success) {
            attempt--;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new ConditionInterruptedException(THREAD_INTERRUPTED_MESSAGE + id);
            }
        }
        return attempt;
    }

    private Condition createCondition(K id) throws ConcurrentConditionException, ConditionInputException {
        if (id == null) {
            throw new ConditionInputException(NULL_ID_MESSAGE);
        }

        Condition condition;

        condition = conditionMap.get(id);
        if (condition != null) {
            throw new ConcurrentConditionException(DIFF_THREAD_CONDITION_BLOCKED_MESSAGE + id);
        }
        condition = lock.newCondition();
        conditionMap.put(id, condition);
        return condition;
    }

    private boolean tryReleaseForCondition(K id) {
        boolean releaseSuccessful = false;
        Condition condition = conditionMap.get(id);
        if (condition != null) {
            lock.lock();
            try {
                conditionMap.remove(id);
                condition.signal();
                releaseSuccessful = true;
            } finally {
                lock.unlock();
            }
        }
        return releaseSuccessful;
    }
}
