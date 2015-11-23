package com.dc.util.condition;

import com.dc.util.condition.exception.ConditionException;

import java.util.concurrent.TimeUnit;

/**
 * BasicConditionalBarrier blocks the calling thread until another thread calls the release method, at which point either returns
 * successfully or throws exception, if cancelled or interrupted.
 *
 */
public class BasicConditionalBarrier {
    private ConditionalBarrier<String> barrier;
    private final String blockCode = "BASIC_CONDITIONAL_BARRIER_BLOCK_CODE";

    public BasicConditionalBarrier() {
        barrier = new ConditionalBarrier<>();
    }

    public void block() throws ConditionException {
        barrier.block(blockCode);
    }

    public void block(long timeout, TimeUnit timeUnit) throws ConditionException {
        barrier.block(blockCode, timeout, timeUnit);
    }

    public void release() throws ConditionException {
        barrier.release(blockCode);
    }

    public void cancel() throws ConditionException {
        barrier.cancel(blockCode);
    }

    public void cancel(Exception exception) throws ConditionException {
        barrier.cancel(blockCode, exception);
    }
}
