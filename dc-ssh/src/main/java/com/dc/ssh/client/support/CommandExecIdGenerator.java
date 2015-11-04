package com.dc.ssh.client.support;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates the unique command execution id.
 */
public class CommandExecIdGenerator {
    private final AtomicInteger counter;
    private String uniqueId;

    public static final char SEPARATOR = '/';
    private static final String OUTPUT_FILE_PREFIX = "DC";
    private static final int UPPER_THRESHOLD = 99999999;

    public CommandExecIdGenerator(String uniqueId) {
        this.uniqueId = uniqueId;
        counter = new AtomicInteger(1);
    }

    public String generate() {
        if(counter.get() > UPPER_THRESHOLD) {
            synchronized (counter) {
                counter.set(1);
            }
        }
        Calendar today = Calendar.getInstance();
        return new StringBuilder().append(today.get(Calendar.YEAR)).append(today.get(Calendar.MONTH)).append(SEPARATOR)
                .append(today.get(Calendar.DAY_OF_MONTH)).append(SEPARATOR)
                .append(today.get(Calendar.HOUR)).append(uniqueId).append(SEPARATOR).append(OUTPUT_FILE_PREFIX)
                .append(today.get(Calendar.MINUTE)).append(today.get(Calendar.SECOND))
                .append(OUTPUT_FILE_PREFIX).append(counter.incrementAndGet()).toString();
    }
}
