package com.dream.flink.func.map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava31.com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * This limiter can process recordsPerSecond, and this map method will have some sleep.
 * It takes 1 second for the caller to call the map method recordsPerSecond times.
 */
public class RateLimiterSleepMapFunction<T> extends RichMapFunction<T, T> {

    private final double eachTimeDurationInNano;
    private long currentCount;
    private long currentDurationInNano;

    public RateLimiterSleepMapFunction(int recordsPerSecond) {
        this.eachTimeDurationInNano = TimeUnit.SECONDS.toNanos(1) * 1.0d / recordsPerSecond;
    }

    @Override
    public void open(Configuration parameters) {
        currentCount = 0;
        currentDurationInNano = 0;
    }

    @Override
    public T map(T value) throws InterruptedException {
        long startTime = System.nanoTime();

        TimeUnit.NANOSECONDS.sleep(getNextSleepTimeInNano());

        currentDurationInNano += (System.nanoTime() - startTime);
        return value;
    }

    private long getNextSleepTimeInNano() {
        currentCount++;
        long expectTotalDuration = (long) eachTimeDurationInNano * currentCount;
        return expectTotalDuration - currentDurationInNano;
    }

    public static void main(String[] args) throws InterruptedException {
        int recordsPerSecond = 10000;
        RateLimiterSleepMapFunction<Object> rateLimiterMap = new RateLimiterSleepMapFunction<>(recordsPerSecond);
        rateLimiterMap.open(new Configuration());

        long startTime = System.currentTimeMillis();
        for (int i = 1; i < recordsPerSecond * 20; i++) {
            if (i % recordsPerSecond == 0) {
                long currentTime = System.currentTimeMillis();
                System.out.println(currentTime - startTime);
                startTime = currentTime;
            }
            rateLimiterMap.map(null);
        }
    }
}