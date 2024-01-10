package com.dream.flink.scheduler.autoscaler;

import com.dream.flink.util.HashUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

/**
 * Test for the busy ratio when TM cpu is not enough.
 *
 * Test Result: the busy ratio of all task are 100%.
 */
public class CpuNotEnoughDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("rest.flamegraph.enabled", "true");

        conf.setString("job.autoscaler.enabled", "true");
        conf.setString("job.autoscaler.scaling.enabled", "true");
        conf.setString("job.autoscaler.stabilization.interval", "1m");
        conf.setString("job.autoscaler.metrics.window", "2m");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(100000000),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")
                .rebalance()
                .addSink(new SinkFunction<Long>() {
                    @Override
                    public void invoke(Long value, Context context) {
                        String s = value.toString();
                        for (int i = 0; i < 1000; i++) {
                            s = HashUtil.md5(s);
                        }
                    }
                })
                .name("MySink");

        env.execute(CpuNotEnoughDemo.class.getSimpleName());
    }
}
