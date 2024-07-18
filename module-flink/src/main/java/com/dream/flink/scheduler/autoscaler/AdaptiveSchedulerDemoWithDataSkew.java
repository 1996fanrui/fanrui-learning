package com.dream.flink.scheduler.autoscaler;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

/**
 * Test for adaptive scheduler with data skew.
 */
public class AdaptiveSchedulerDemoWithDataSkew {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set(RestOptions.PORT, 8081);
        conf.setString("taskmanager.numberOfTaskSlots", "100");
        conf.setString("execution.checkpointing.unaligned.enabled", "true");

        conf.setString("jobmanager.scheduler", "adaptive");
        conf.setString("job.autoscaler.enabled", "true");
        conf.setString("job.autoscaler.scaling.enabled", "true");
        conf.setString("job.autoscaler.stabilization.interval", "10s");
        conf.setString("job.autoscaler.metrics.window", "1m");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.setParallelism(5);
        env.enableCheckpointing(5_000L);

        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(1000),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")
                .keyBy(new KeySelector<Long, Long>() {
                    @Override
                    public Long getKey(Long value) throws Exception {
                        return 0L;
                    }
                })
                .map(new RichMapFunction<Long, Long>() {
                    @Override
                    public Long map(Long value) throws Exception {
                        Thread.sleep(10);
                        return value;
                    }
                })
                .name("RateLimiterMapFunction")
                .rebalance()
                .addSink(new DiscardingSink<>())
                .name("MySink");

        env.execute(AdaptiveSchedulerDemoWithDataSkew.class.getSimpleName());
    }

}
