package com.dream.flink.scheduler.autoscaler;

import com.dream.flink.func.flatmap.DynamicFlatMapper;
import com.dream.flink.func.map.RateLimiterSleepMapFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.v2.DiscardingSink;

import java.time.Duration;

/**
 * Test for adaptive scheduler with dynamic rate.
 */
public class AdaptiveSchedulerWithDynamicRateDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("taskmanager.numberOfTaskSlots", "100");
        conf.setString("rest.flamegraph.enabled", "true");

        conf.setString("jobmanager.scheduler", "adaptive");
        conf.setString("job.autoscaler.enabled", "true");
        conf.setString("job.autoscaler.scaling.enabled", "true");
        conf.setString("job.autoscaler.stabilization.interval", "1m");
        conf.setString("job.autoscaler.metrics.window", "2m");
        conf.setString("job.autoscaler.scale-down.interval", "5m");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(5);

        env.enableCheckpointing(1000L);

        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(100),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")
                .flatMap(new DynamicFlatMapper<>(Duration.ofMinutes(3), new int[]{7,7,8,9}))
                .rebalance()
                .map(new RateLimiterSleepMapFunction<>(100))
                .name("RateLimiterMapFunction")
                .rebalance()
                .sinkTo(new DiscardingSink<>())
                .name("MySink");

        env.execute(AdaptiveSchedulerWithDynamicRateDemo.class.getSimpleName());
    }

}
