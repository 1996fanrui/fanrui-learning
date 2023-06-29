package com.dream.flink.optimize.dynamic.rebalance;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.shaded.guava30.com.google.common.util.concurrent.RateLimiter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 模拟 hdfs 慢节点，验证 dynamic rebalance 对该类型问题的容忍。
 * <p>
 * 默认行为：五分之一的 subtask，每处理五分钟数据会 sleep 1-2 分钟。其他 subtask 一直按照 10k records/s 的速率进行消费。
 */
public class MockHdfsSlowNodeDemo {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int dataLength = parameterTool.getInt("dataLength", 320);
        int recordsPerSecond = parameterTool.getInt("recordsPerSecond", 10_000);
        long sleepInterval = parameterTool.getLong("sleepInterval", TimeUnit.MINUTES.toMillis(5));

        final StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> stream = env.addSource(new StringRandomSource(dataLength))
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofMillis(10))
                                .withTimestampAssigner((ctx) -> (element, recordTimestamp) -> System.currentTimeMillis()))
                .rebalance();

        stream.map(new SlowSubtaskMap<>(recordsPerSecond, (int) sleepInterval)).addSink(new DiscardingSink<>());

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

    /**
     * 周期性波动的 map 来模拟 hdfs 慢节点的场景。
     *
     * @param <T>
     */
    private static class SlowSubtaskMap<T> extends RichMapFunction<T, T> {

        private final int recordsPerSecond;

        private final int sleepInterval;

        private static final Random RANDOM = new Random();

        private RateLimiter rateLimiter;

        private long nextSleepTimestamp;

        private SlowSubtaskMap(int recordsPerSecond, int sleepInterval) {
            this.recordsPerSecond = recordsPerSecond;
            this.sleepInterval = sleepInterval;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            this.rateLimiter = RateLimiter.create(recordsPerSecond);
            this.nextSleepTimestamp = System.currentTimeMillis() + RANDOM.nextInt(sleepInterval);
        }

        @Override
        public T map(T value) throws Exception {
            checkAndSleep();
            rateLimiter.acquire();

            return value;
        }

        private void checkAndSleep() throws InterruptedException {
            if (getRuntimeContext().getIndexOfThisSubtask() % 5 != 0) {
                return;
            }
            if (nextSleepTimestamp > System.currentTimeMillis()) {
                return;
            }
            int sleepSecond = 60 + RANDOM.nextInt(60);
            TimeUnit.SECONDS.sleep(sleepSecond);
            nextSleepTimestamp = System.currentTimeMillis() + sleepInterval;
        }
    }

}
