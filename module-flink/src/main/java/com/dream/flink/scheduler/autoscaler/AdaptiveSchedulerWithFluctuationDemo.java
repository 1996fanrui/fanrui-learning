package com.dream.flink.scheduler.autoscaler;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.time.Duration;

/**
 * Test for busy time metric with fluctuation demo.
 */
public class AdaptiveSchedulerWithFluctuationDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.enableCheckpointing(1000L);
        env.setParallelism(1);

        env.addSource(new ParallelSourceFunction<Integer>() {
            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<Integer> ctx) throws Exception {
                while (isRunning) {
                    synchronized (ctx.getCheckpointLock()) {
                        for (int i = 0; i < 1000; i++) {
                            ctx.collect(1);
                        }
                    }
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        })
                .rebalance()
                .map(new FluctuationMapper<>(Duration.ofMillis(10), Duration.ofSeconds(10)))
                .name("RateLimiterMapFunction")
                .rebalance()
                .addSink(new DiscardingSink<>())
                .name("MySink");

        env.execute(AdaptiveSchedulerWithFluctuationDemo.class.getSimpleName());
    }

    public static class FluctuationMapper<T> extends RichMapFunction<T, T> {

        private final long sleepTimeMs;
        private final long fluctuationPeriodMs;

        private boolean isSleep;
        private long lastChangeTime;

        public FluctuationMapper(Duration sleepTime, Duration fluctuationPeriod) {
            this.sleepTimeMs = sleepTime.toMillis();
            this.fluctuationPeriodMs = fluctuationPeriod.toMillis();
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            isSleep = false;
            lastChangeTime = System.currentTimeMillis();
        }

        @Override
        public T map(T value) throws Exception {
            final long now = System.currentTimeMillis();
            if (isSleep) {
                Thread.sleep(sleepTimeMs);
                if (now - lastChangeTime > fluctuationPeriodMs) {
                    isSleep = false;
                    lastChangeTime = now;
                }
            } else {
                if (now - lastChangeTime > fluctuationPeriodMs) {
                    isSleep = true;
                    lastChangeTime = now;
                }
            }
            return value;
        }
    }
}
