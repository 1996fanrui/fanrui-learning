package com.dream.flink.uc;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.AsyncFunction;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.streaming.api.functions.sink.v2.DiscardingSink;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * FLINK-34704: Reproduce the UC doesn't work well with Async Operator when internal buffer is full.
 */
public class AsyncFunctionInternalBufferFullWithUC {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        conf.setString("execution.checkpointing.unaligned.enabled", "true");
        conf.setString("execution.checkpointing.interval", "5s");
        conf.setString("execution.checkpointing.min-pause", "3s");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        env.setParallelism(1);

        AsyncDataStream.orderedWait(
                        env.fromSequence(Long.MIN_VALUE, Long.MAX_VALUE).shuffle(),
                        new AsyncFunction<Long, Long>() {
                            @Override
                            public void asyncInvoke(Long aLong, ResultFuture<Long>
                                    resultFuture) {
                            }
                        },
                        24,
                        TimeUnit.HOURS,
                        1)
                .print();

        env.execute("AsyncFunctionInternalBufferFullWithUC");

    }

}