package com.dream.flink.sql.profile.sink;

import org.apache.flink.types.Row;

import java.util.concurrent.TimeUnit;

public class RequestHBaseSink extends AbstractSink {

    @Override
    protected void doProcess(Row value, Context context) throws Exception {
        requestHBase();
    }

    private void requestHBase() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

}
