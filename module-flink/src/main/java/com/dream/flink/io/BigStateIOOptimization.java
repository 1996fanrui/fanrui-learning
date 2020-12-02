package com.dream.flink.io;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.state.restore.ide.CheckpointRestoreByIDEUtils;
import com.dream.flink.util.CheckpointUtil;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

/**
 * @author fanrui03
 * @date 2020/10/31 14:20
 */
public class BigStateIOOptimization {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        CheckpointUtil.setFsStateBackend(env);
        env.setParallelism(1);

        EnvironmentSettings mySetting = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, mySetting);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
                .filter(Objects::nonNull);

        tableEnv.createTemporaryView("order_table", orderStream,
                "ts, orderId, userId, goodsId, price, cityId");

        Table query = tableEnv.sqlQuery("select count(distinct userId) from order_table");

        DataStream<Tuple2<Boolean, Row>> uvStream = tableEnv.toRetractStream(query, Row.class);

        uvStream.filter((FilterFunction<Tuple2<Boolean, Row>>) value -> value.f0)
                .map((MapFunction<Tuple2<Boolean, Row>, Row>) value -> value.f1)
                .print();

        env.execute(BigStateIOOptimization.class.getSimpleName());
//        String externalCheckpoint = "file:///Users/fanrui03/Documents/tmp/checkpoint/f74a3a6af248b9aaeb81f61f83164c31/chk-1";
//        CheckpointRestoreByIDEUtils.run(env.getStreamGraph(), externalCheckpoint);
    }
}
