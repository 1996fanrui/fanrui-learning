package com.dream.flink.func.aggregate;

import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * Count 专用的Aggregate
 *
 * @author fanrui
 * @time 2020-01-09 02:43:08
 */
public class StringConcatAggregate<IN> implements AggregateFunction<IN, String, String> {

    @Override
    public String createAccumulator() {
        return "";
    }

    @Override
    public String add(IN value, String accumulator) {
        return accumulator + "---------\n" + value;
    }

    @Override
    public String getResult(String accumulator) {
        return accumulator;
    }

    @Override
    public String merge(String a, String b) {
        return a + "---------\n" + b;
    }
}
