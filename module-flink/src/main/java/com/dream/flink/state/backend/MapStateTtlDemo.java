package com.dream.flink.state.backend;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.state.restore.ide.CheckpointRestoreByIDEUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 满足以下条件时，容易复现 MapState FsStateBackend ttl 很长的问题：
 * 1. keyBY 的 key 数量 140
 * 2. parallelism 1
 * 3. maxParallelism 2
 * 4. MapState 中元素较多，大于 10000
 *
 * 其他组合也会有问题，例如上面的前三项比例不变，共同 * 系数
 */
public class MapStateTtlDemo {

    private static final Logger LOG = LoggerFactory.getLogger(CheckpointRestoreByIDEUtils.class);

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setMaxParallelism(2);

        env.addSource(new OrderGenerator())
                .keyBy(Order::getCityId)
                .map(new RichMapFunction<Order, String>() {

                    transient MapState<String, Boolean> userSet;

                    @Override
                    public void open(Configuration parameters) {
                        MapStateDescriptor<String, Boolean> stateDescriptor =
                                new MapStateDescriptor<>("userSet", String.class, Boolean.class);

                        StateTtlConfig stateTtlConfig = new StateTtlConfig.Builder(Time.hours(2))
                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                .build();

                        stateDescriptor.enableTimeToLive(stateTtlConfig);

                        this.userSet = getRuntimeContext().getMapState(stateDescriptor);
                    }

                    @Override
                    public String map(Order value) throws Exception {
                        if (userSet.contains(value.userId)) {
                            return String.format("cityId %d userId %s is a old user.", value.getCityId(), value.getUserId());
                        }

                        userSet.put(value.userId, true);
                        return String.format("cityId %d userId %s is a new user.", value.getCityId(), value.getUserId());
                    }
                })
                .print();

        env.execute(MapStateTtlDemo.class.getSimpleName());
    }

}
