package com.dream.flink.func.aggregate;

import org.apache.flink.api.common.functions.AggregateFunction;


/**
 * Sum 专用的Aggregate
 * @author fanrui
 * @time  2020-03-29 22:30:23
 */
public class SumAggregate<IN> implements AggregateFunction<IN, Long, Long> {

    // IN 类型转 Long 类型的 function，便于计算 Sum 值
    private ToLongFunction<IN> function;

    public SumAggregate(ToLongFunction<IN> function) {
        this.function = function;
    }

    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(IN value, Long sum) {
        return sum + function.applyAsLong(value);
    }

    @Override
    public Long getResult(Long sum) {
        return sum;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}
