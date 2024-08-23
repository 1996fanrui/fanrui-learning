package com.dream.flink.func.flatmap;


import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.time.Instant;

import static org.apache.flink.util.Preconditions.checkArgument;

/**
 * 在 duration 的时间范围内，使用相应的 膨胀系数。 例如:
 * Duration[] durations = {Duration.ofMinutes(1), Duration.ofMinutes(2), Duration.ofMinutes(1)};
 * int[] expansionFactors = {2, 4, 3};
 * 表示 第一分钟的膨胀系数是 2，接下来的 2 分钟 膨胀系数是 4，接下来的 1 分钟 膨胀系数是 3。
 * 依次循环。
 */
public class DynamicFlatMapper<T> extends RichFlatMapFunction<T, T> {

    private final Duration[] durations;
    private final int[] expansionFactors;

    private Instant currentDurationStartTime;
    private int currentDurationIndex;

    public DynamicFlatMapper(Duration fixedDuration, int[] expansionFactors) {
        this(new Duration[expansionFactors.length], expansionFactors);
        for (int i = 0; i < expansionFactors.length; i++) {
            durations[i] = fixedDuration;
        }
    }

    public DynamicFlatMapper(Duration[] durations, int[] expansionFactors) {
        checkArgument(expansionFactors.length > 0);
        checkArgument(durations.length == expansionFactors.length);
        this.durations = durations;
        this.expansionFactors = expansionFactors;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        currentDurationStartTime = Instant.now();
        currentDurationIndex = 0;
    }

    @Override
    public void flatMap(T value, Collector<T> out) {
        if (currentDurationStartTime.plus(durations[currentDurationIndex]).isBefore(Instant.now())) {
            // skip to the next duration if (now > start_time + duration)
            currentDurationStartTime = Instant.now();
            currentDurationIndex = (++currentDurationIndex) % durations.length;
        }

        int expansionFactor = expansionFactors[currentDurationIndex];
        if (expansionFactor <= 0) {
            return;
        }

        for (int i = 0; i < expansionFactor; i++) {
            out.collect(value);
        }
    }
}
