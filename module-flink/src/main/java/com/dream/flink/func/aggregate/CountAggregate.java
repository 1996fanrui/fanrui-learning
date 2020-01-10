package com.dream.flink.func.aggregate;

import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * Count 专用的Aggregate
 * @author fanrui
 * @time  2020-01-09 02:43:08
 */
public class CountAggregate<IN> implements AggregateFunction<IN, Long, Long> {
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(IN value, Long accumulator) {
        return accumulator + 1;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}
