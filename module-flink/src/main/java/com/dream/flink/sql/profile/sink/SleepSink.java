package com.dream.flink.sql.profile.sink;

import org.apache.flink.types.Row;

import java.util.concurrent.TimeUnit;

public class SleepSink extends AbstractSink {

    @Override
    protected void doProcess(Row value, Context context) throws Exception {
        TimeUnit.MILLISECONDS.sleep(100);
    }

}
