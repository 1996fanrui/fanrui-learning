package com.dream.flink.network;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 1. 500MB, usage 250MB (free 5 slots)
 * 2. 500MB, usage 250MB (free 0 slots)
 *
 * 3. Based on the PR, we will change the managed size to 250MB or adjust the managed fraction
 *   And then the total is 300 MB, actual usage will be changed to 125 MB.
 *   150 MB
 *
 *  500 MB total -> 250 MB usage
 *  300 MB total -> 150 MB usage
 *  200 MB  total ->  100MB
 *
 *   The managed memory usage means how much memory flink assign to rocksdb
 *   It's not how much memory rocksdb actual uses.
 */
public class ManagedMemoryDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("taskmanager.numberOfTaskSlots", "10");
        conf.setString("rest.flamegraph.enabled", "true");

        conf.setString("state.backend.type", "rocksdb");
        conf.setString("taskmanager.memory.managed.size", "300 MB");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(5);
        env.disableOperatorChaining();

        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(10),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")//.setParallelism(10)
                .keyBy(new KeySelector<Long, Long>() {
                    @Override
                    public Long getKey(Long value) {
                        return 0L;
                    }
                })
                .map(new CountMapFunc())
                .rebalance()
                .print();

        env.execute(ManagedMemoryDemo.class.getSimpleName());
    }

    public static class CountMapFunc extends RichMapFunction<Long, Long> {

        ValueState<Long> counter;

        @Override
        public void open(Configuration parameters) throws Exception {

            counter = getRuntimeContext().getState(
                    new ValueStateDescriptor<>("timerState", Long.class));        }

        @Override
        public Long map(Long value) throws Exception {
            final Long count = counter.value();
            if (count == null) {
                counter.update(1L);
            } else {
                counter.update(count + 1);
            }
            return counter.value();
        }
    }

}
