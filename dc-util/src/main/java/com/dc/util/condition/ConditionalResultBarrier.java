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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.dc.util.condition.exception.ConditionCancelledException;
import com.dc.util.condition.exception.ConditionException;
import com.dc.util.condition.exception.ConditionResultException;
import com.dc.util.condition.exception.ConditionTimeoutException;

/**
 * ConditionalResultBarrier blocks on a given condition until the condition is met, at which point either returns the expected
 * result or throws exception, if cancelled or interrupted.
 *
 */
public class ConditionalResultBarrier<I, R> {
    private ConditionalThreadBlocker<I> threadBlocker;
    private Map<I, Object>              resultMap;

    public ConditionalResultBarrier() {
        threadBlocker = new ConditionalThreadBlocker<I>(new ConditionConfiguration.Builder().build());
        resultMap = new ConcurrentHashMap<I, Object>();
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
        return threadBlocker.purge(oldTimeInMillis);
    }

    @SuppressWarnings({ "unchecked" })
    public R block(I id) throws ConditionException {

        Object result;
        ExitStatus status = threadBlocker.blockOnCondition(id);
        result = resultMap.remove(id);

        if (ExitStatus.CANCELLED == status) {
            throw new ConditionCancelledException("cancel() called for the condition : " + id, (Exception) result);
        }

        if (result != null && result instanceof Exception) {
            throw new ConditionResultException((Exception) result);
        }

        return (R) result;
    }

    @SuppressWarnings({ "unchecked" })
    public R block(I id, long timeout, TimeUnit timeUnit) throws ConditionException {

        ExitStatus status = threadBlocker.blockOnCondition(id, timeout, timeUnit);
        Object result = resultMap.remove(id);

        if (ExitStatus.CANCELLED == status) {
            throw new ConditionCancelledException("cancel() called for the condition : " + id, (Exception) result);
        } else if (ExitStatus.TIMEOUT == status) {
            throw new ConditionTimeoutException("Timeout occurred for the condition : " + id);
        }

        if (result != null && result instanceof Exception) {
            throw new ConditionResultException((Exception) result);
        }

        return (R) result;
    }

    public void release(I id, R result) throws ConditionException {
        resultMap.put(id, result);
        threadBlocker.releaseForCondition(id);
    }

    public void cancel(I id) throws ConditionException {
        threadBlocker.cancelForCondition(id);
    }

    public void cancel(I id, Exception exception) throws ConditionException {
        resultMap.put(id, exception);
        threadBlocker.cancelForCondition(id);
    }
}
