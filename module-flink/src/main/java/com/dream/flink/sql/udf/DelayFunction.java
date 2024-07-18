package com.dream.flink.sql.udf;

import org.apache.flink.table.functions.ScalarFunction;

import java.util.concurrent.TimeUnit;

/**
 * The delay function that control the delay time as accurately as possible.
 */
public class DelayFunction extends ScalarFunction {

    private final double eachTimeDurationInNano;
    private long currentActualDurationInNano;
    private long currentExpectedDurationInNano;

    public DelayFunction() {
        // Process 100 records per second by default.
        int recordsPerSecond = 100;
        this.eachTimeDurationInNano = TimeUnit.SECONDS.toNanos(1) * 1.0d / recordsPerSecond;
        currentActualDurationInNano = 0;
        currentExpectedDurationInNano = 0;
    }

    public int eval(Integer value) throws Exception {
        sleep(eachTimeDurationInNano);
        return value;
    }

    public int eval(Integer value, Integer sleepMs) throws Exception {
        sleep(TimeUnit.MILLISECONDS.toNanos(sleepMs));
        return value;
    }

    private void sleep(double nano) throws Exception {
        long startTime = System.nanoTime();
        TimeUnit.NANOSECONDS.sleep(getNextSleepTimeInNano(nano));
        currentActualDurationInNano += (System.nanoTime() - startTime);
    }

    private long getNextSleepTimeInNano(double nano) {
        currentExpectedDurationInNano += nano;
        return currentExpectedDurationInNano - currentActualDurationInNano;
    }

}
