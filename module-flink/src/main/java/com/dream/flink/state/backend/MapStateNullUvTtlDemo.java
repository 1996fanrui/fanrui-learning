package com.dream.flink.state.backend;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

/**
 * 模拟 map state 的 user value 是 null 的 case
 *
 */
public class MapStateNullUvTtlDemo {

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.setString("state.backend.type", "hashmap");
//        conf.setString("state.backend.type", "rocksdb");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.enableCheckpointing(5000);
        env.setParallelism(1);

        env.fromSource(
                        new DataGeneratorSource<>(
                                value -> value,
                                Long.MAX_VALUE,
                                RateLimiterStrategy.perSecond(1),
                                Types.LONG),
                        WatermarkStrategy.noWatermarks(),
                        "Source Task").setParallelism(2)
                .keyBy(
                        new KeySelector<Long, Integer>() {
                            @Override
                            public Integer getKey(Long value) throws Exception {
                                return value.hashCode() % 100;
                            }
                        })
                .map(new RichMapFunction<Long, String>() {

                    transient MapState<Long, Boolean> ids;

                    @Override
                    public void open(OpenContext parameters) {
                        MapStateDescriptor<Long, Boolean> stateDescriptor =
                                new MapStateDescriptor<>("ids", Long.class, Boolean.class);

                        StateTtlConfig stateTtlConfig = StateTtlConfig
                                .newBuilder(Duration.ofHours(2))
                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                .build();

                        stateDescriptor.enableTimeToLive(stateTtlConfig);

                        this.ids = getRuntimeContext().getMapState(stateDescriptor);
                    }

                    @Override
                    public String map(Long id) throws Exception {
                        if (ids.contains(id)) {
                            return String.format("%d is a old id.", id);
                        }

                        ids.put(id, null);
                        return String.format("%d is a new id.", id);
                    }
                })
                .print();

        env.execute(MapStateNullUvTtlDemo.class.getSimpleName());
    }


}
