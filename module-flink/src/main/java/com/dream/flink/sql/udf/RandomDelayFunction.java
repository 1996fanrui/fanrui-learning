package com.dream.flink.sql.udf;

import org.apache.flink.table.functions.ScalarFunction;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The delay function that control the delay time via sleep directly.
 */
public class RandomDelayFunction extends ScalarFunction {

    public RandomDelayFunction() {
    }

    public int eval(Integer sleepMs) throws Exception {
        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(Math.max(sleepMs, 0) + 1));
        return 1;
    }

    public static void main(String[] args) throws Exception {
        RandomDelayFunction randomDelayFunction = new RandomDelayFunction();
        randomDelayFunction.eval(0);
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
        System.out.println(new Random().nextInt(3));
    }

}
