package com.dream.flink.optimize.dynamic.rebalance;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava31.com.google.common.util.concurrent.RateLimiter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 模拟部分 subtask 持续处理慢的场景，验证 dynamic rebalance 对该类型问题的容忍。
 * <p>
 * 默认行为：五分之一的 subtask，一直按照 10 records/s 的速率进行消费，其他 subtask 一直按照 10k records/s 的速率进行消费。
 */
public class MockSlowSubtaskDemo {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int dataLength = parameterTool.getInt("dataLength", 320);
        int recordsPerSecond = parameterTool.getInt("recordsPerSecond", 10_000);
        int recordsPerSecondForSlowSubtask = parameterTool.getInt("recordsPerSecondForSlowSubtask", 10);

        final StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> stream = env.addSource(new StringRandomSource(dataLength))
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofMillis(10))
                                .withTimestampAssigner((ctx) -> (element, recordTimestamp) -> System.currentTimeMillis()))
                .rebalance();

        stream.map(new SlowSubtaskMap<>(recordsPerSecond, recordsPerSecondForSlowSubtask)).addSink(new DiscardingSink<>());

        env.execute("Streaming Rebalance");
    }

    private static class StringRandomSource implements ParallelSourceFunction<String> {
        volatile boolean isRunning = true;
        private final int dataLength;

        private StringRandomSource(int dataLength) {
            this.dataLength = dataLength;
        }

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            RandomGenerator<String> generator = RandomGenerator.stringGenerator(dataLength);
            generator.open(null, null, null);
            while (isRunning) {
                synchronized (ctx.getCheckpointLock()) {
                    ctx.collect(generator.next());
                }
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    private static class SlowSubtaskMap<T> extends RichMapFunction<T, T> {

        private final int recordsPerSecond;

        private final int recordsPerSecondForSlowSubtask;

        private RateLimiter rateLimiter;

        private SlowSubtaskMap(int recordsPerSecond, int recordsPerSecondForSlowSubtask) {
            this.recordsPerSecond = recordsPerSecond;
            this.recordsPerSecondForSlowSubtask = recordsPerSecondForSlowSubtask;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            int indexOfThisSubtask = getRuntimeContext().getIndexOfThisSubtask();
            int finalRecordsPerSecond = indexOfThisSubtask % 5 == 0 ? recordsPerSecondForSlowSubtask : recordsPerSecond;
            this.rateLimiter = RateLimiter.create(finalRecordsPerSecond);
        }

        @Override
        public T map(T value) throws Exception {
            rateLimiter.acquire();
            return value;
        }
    }

}
