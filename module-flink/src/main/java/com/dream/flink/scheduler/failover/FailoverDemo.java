package com.dream.flink.scheduler.failover;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;

/**
 * FailoverDemo with legacy source to avoid version conflict. (It could run with any flink version)
 */
public class FailoverDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        env.addSource(new LimitedSource(), "Data Generator")
                .map(new MapFunction<String, String>() {
                    @Override
                    public String map(String value) {
                        throw new RuntimeException("Expected exception.");
                    }
                });

        env.execute(FailoverDemo.class.getSimpleName());
    }

    public static class LimitedSource extends RichParallelSourceFunction<String> {
        volatile boolean isRunning = true;

        public LimitedSource() {
        }

        @Override
        public void run(SourceContext sourceContext) throws Exception {
            RandomGenerator<String> generator = RandomGenerator.stringGenerator(1000);
            generator.open(null, null, null);
            while (isRunning) {
                Thread.sleep(100);
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
