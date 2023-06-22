package com.dream.flink.sql;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

/**
 * @author fanrui03
 * @date 2020/9/20 14:19
 */
public class SimpleQueryDemo {

    public static void main(String[] args) throws Exception {
        EnvironmentSettings mySetting = EnvironmentSettings
            .newInstance()
            .inStreamingMode()
            .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, mySetting);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
            .filter(Objects::nonNull);

        tableEnv.createTemporaryView("order_table", orderStream);

        Table query = tableEnv.sqlQuery("select *, md5(userId), concat_ws('~', userId, orderId) from order_table");

        DataStream<Row> orderDataStream = tableEnv.toAppendStream(query, Row.class);
        orderDataStream.print();

        env.execute(SimpleQueryDemo.class.getSimpleName());
    }

}
