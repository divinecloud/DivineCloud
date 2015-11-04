package com.dc.ssh.client.support;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique sequential ID.
 */
public class SequentialIdGenerator {

    private AtomicInteger counter;
    private int           threshold;
    private int           startCount;

    public SequentialIdGenerator(int startCount) {

        this.startCount = startCount;
        counter = new AtomicInteger(startCount);
        threshold = Integer.MAX_VALUE - 10000;
    }

    public int generate() {
        int id = counter.incrementAndGet();
        if (id >= threshold) {
            synchronized (this) {
                if (counter.get() >= threshold) {
                    counter = new AtomicInteger(startCount);
                }
                id = counter.incrementAndGet();
            }
        }
        return id;
    }
}
