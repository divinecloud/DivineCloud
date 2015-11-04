package com.dc.api.support;

import java.util.concurrent.atomic.AtomicLong;

public class ExecutionIdGenerator {
    private AtomicLong counter;
    private static String prefix = "ExecId-" + System.nanoTime() + Math.round(Math.random());

    public ExecutionIdGenerator() {
        counter = new AtomicLong(1);
    }

    public String next() {
        return prefix + counter.incrementAndGet();
    }
}
