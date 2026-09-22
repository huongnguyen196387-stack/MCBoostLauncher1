package com.mcboost.launcher;

import java.util.concurrent.atomic.AtomicLong;

final class CpsCounter {
    private static final AtomicLong lastWindow = new AtomicLong(System.currentTimeMillis());
    private static final AtomicLong clicks = new AtomicLong(0);

    private CpsCounter() {}

    static void record() {
        long now = System.currentTimeMillis();
        long start = lastWindow.get();
        if (now - start >= 1000) {
            lastWindow.set(now);
            clicks.set(0);
        }
        clicks.incrementAndGet();
    }

    static int getAndMaybeReset() {
        long now = System.currentTimeMillis();
        long start = lastWindow.get();
        if (now - start >= 1000) {
            lastWindow.set(now);
            return (int) clicks.getAndSet(0);
        }
        return (int) clicks.get();
    }
}
