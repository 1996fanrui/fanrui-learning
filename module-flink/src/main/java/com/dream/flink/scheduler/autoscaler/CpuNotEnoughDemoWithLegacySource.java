package com.dream.flink.scheduler.autoscaler;

import com.dream.flink.util.HashUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test for the busy ratio when TM cpu is not enough.
 *
 * Exhausting the TM CPU at the thread pool.
 */
public class CpuNotEnoughDemoWithLegacySource {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("rest.flamegraph.enabled", "true");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        env.addSource(new LimitedSource(), "Data Generator")
                .addSink(new DiscardingSink<>())
                .name("MySink");

        env.execute(CpuNotEnoughDemoWithLegacySource.class.getSimpleName());
    }


    public static class LimitedSource extends RichParallelSourceFunction<String> {
        private ExecutorService executorService;
        volatile boolean isRunning = true;

        public LimitedSource() {
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            int threadCount = 10;
            this.executorService = Executors.newFixedThreadPool(threadCount);
            for (int i = 0; i < 10; i++) {
                executorService.execute(() -> {
                    try {
                        String a = "1";
                        while (true) {
                            a = HashUtil.md5(a);
                            Thread.sleep(0);
                        }
                    } catch (Throwable ignored) {

                    }
                });
            }
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
            executorService.shutdownNow();
        }
    }
}
