package com.dream.flink.func.map;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava31.com.google.common.util.concurrent.RateLimiter;

/**
 * This limiter can process recordsPerSecond.
 * If the actual process rate is less than recordsPerSecond,
 * the map method will return directly, and the duration is 0.
 */
public class RateLimiterMapFunction<T> extends RichMapFunction<T, T> {

    private final int recordsPerSecond;

    private RateLimiter rateLimiter;

    public RateLimiterMapFunction(int recordsPerSecond) {
        this.recordsPerSecond = recordsPerSecond;
    }

    @Override
    public void open(Configuration parameters) {
        rateLimiter = RateLimiter.create(recordsPerSecond);
    }

    @Override
    public T map(T value) {
        rateLimiter.acquire();
        return value;
    }

    public static void main(String[] args) {
        int recordsPerSecond = 100000;
        RateLimiterMapFunction<Object> rateLimiterMap = new RateLimiterMapFunction<>(recordsPerSecond);
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