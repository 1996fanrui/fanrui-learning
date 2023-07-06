package com.dream.flink.func.source;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava30.com.google.common.util.concurrent.RateLimiter;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;

public class RateLimitedSourceDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.addSource(new LimitedSource(1000000)).name("heavy source")
                .filter(record -> false)
                .rebalance().addSink(new DiscardingSink<>());

        env.addSource(new LimitedSource(1)).name("light source")
                .filter(record -> false)
                .rebalance().addSink(new DiscardingSink<>());

        env.execute();
    }

    public static class LimitedSource extends RichParallelSourceFunction<String> {
        private final double permitsPerSecond;
        private RateLimiter rateLimiter;
        volatile boolean isRunning = true;

        public LimitedSource(double permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            this.rateLimiter = RateLimiter.create(permitsPerSecond);
        }

        @Override
        public void run(SourceContext sourceContext) throws Exception {
            RandomGenerator<String> generator = RandomGenerator.stringGenerator(1000);
            generator.open(null, null, null);
            while (isRunning) {
                rateLimiter.acquire();
                synchronized (sourceContext.getCheckpointLock()) {
                    sourceContext.collect(generator.next());
                }
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }
}
