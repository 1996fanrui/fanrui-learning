package com.dream.flink.sql.udf;

import org.apache.flink.table.functions.ScalarFunction;

import java.util.concurrent.TimeUnit;

/**
 * The function will return the input as the result directly.
 * CREATE FUNCTION returnInput AS 'com.dream.flink.sql.udf.ReturnInputFunction' USING JAR 'xxx';
 */
public class ReturnInputFunction extends ScalarFunction {

    public ReturnInputFunction() {
    }

    public int eval(Integer value) throws Exception {
        return value;
    }

}
