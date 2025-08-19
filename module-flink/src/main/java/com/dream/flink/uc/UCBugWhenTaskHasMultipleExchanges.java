package com.dream.flink.uc;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It could reproduce this issue: https://issues.apache.org/jira/browse/FLINK-38267
 *
 * Caused by: java.lang.UnsupportedOperationException: Cannot rescale the given pointwise partitioner.
 * Did you change the partitioner to forward or rescale?
 * It may also help to add an explicit shuffle().
 */
public class UCBugWhenTaskHasMultipleExchanges {
    private static final Logger LOG = LoggerFactory.getLogger(UCBugWhenTaskHasMultipleExchanges.class);

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        conf.setString("rest.port", "12348");
        conf.setString("execution.checkpointing.unaligned.enabled", "true");
        conf.setString("execution.checkpointing.interval", "10s");
        conf.setString("execution.checkpointing.min-pause", "8s");
        conf.setString("jobmanager.scheduler", "adaptive");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.disableOperatorChaining();

        env.setParallelism(5);

        SingleOutputStreamOperator<String> stream1 = env.fromSource(
                        new DataGeneratorSource<>(
                                value -> new RandomDataGenerator().nextHexString(300),
                                Long.MAX_VALUE,
                                RateLimiterStrategy.perSecond(100000),
                                Types.STRING),
                        WatermarkStrategy.noWatermarks(),
                        "Source Task");

        stream1
                .keyBy(new KeySelectorFunction())
                .map(x -> {
                    Thread.sleep(50);
                    return x;
                }).name("Map after hash");

        stream1.map(x -> {
                    Thread.sleep(5);
                    return x;
            }).name("Map after forward");

        env.execute(UCBugWhenTaskHasMultipleExchanges.class.getSimpleName());
    }

    private static class KeySelectorFunction implements KeySelector<String, Integer> {
        @Override
        public Integer getKey(String value) throws Exception {
            return 0;
        }
    }

}
