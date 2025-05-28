package com.dream.flink.sql.udf;

import org.apache.flink.table.functions.ScalarFunction;

import java.util.concurrent.TimeUnit;

/**
 * The delay function that control the delay time via sleep directly.
 */
public class DelayFunction2 extends ScalarFunction {

    public DelayFunction2() {
    }

    public int eval(Integer value) throws Exception {
        TimeUnit.MILLISECONDS.sleep(10);
        return value;
    }

    public int eval(Integer value, Integer sleepMs) throws Exception {
        TimeUnit.MILLISECONDS.sleep(sleepMs);
        return value;
    }

}
