package com.dream.flink.scheduler.autoscaler;

import com.dream.flink.func.map.RateLimiterSleepMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.Random;

/**
 * Test for adaptive scheduler with legacy source.
 */
public class AdaptiveSchedulerWithLegacySourceDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("rest.port", "8081");
        conf.setString("taskmanager.numberOfTaskSlots", "100");

        conf.setString("rest.flamegraph.enabled", "true");

        conf.setString("jobmanager.scheduler", "adaptive");
        conf.setString("job.autoscaler.enabled", "true");
        conf.setString("job.autoscaler.scaling.enabled", "false");
        conf.setString("job.autoscaler.stabilization.interval", "1m");
        conf.setString("job.autoscaler.metrics.window", "2m");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(5);
        env.enableCheckpointing(1000L);

        env.addSource(new IntegerRandomSource()).setParallelism(1)
                .rebalance()
                .map(new RateLimiterSleepMapFunction<>(100))
                .name("RateLimiterMapFunction")
                .rebalance()
                .addSink(new DiscardingSink<>()).setParallelism(1)
                .name("MySink");

        env.execute(AdaptiveSchedulerWithLegacySourceDemo.class.getSimpleName());
    }

    private static class IntegerRandomSource implements ParallelSourceFunction<Integer> {
        volatile boolean isRunning = true;

        private IntegerRandomSource() {
        }

        @Override
        public void run(SourceContext<Integer> ctx) throws Exception {
            Random random = new Random();
            while (isRunning) {
                synchronized (ctx.getCheckpointLock()) {
                    ctx.collect(random.nextInt());
                }
                Thread.sleep(1);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

}
