package com.dream.flink.sql.udf;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.types.Row;

import java.util.Objects;

public class UDFDemo {

    public static void main(String[] args) throws Exception {

        EnvironmentSettings mySetting = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, mySetting);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
                .filter(Objects::nonNull);

        tableEnv.createTemporaryView("order_table", orderStream,
                "ts, orderId, userId, goodsId, price, cityId");

        tableEnv.registerFunction("hashCode", new HashCode());

        Table query = tableEnv.sqlQuery("select orderId, hashCode(orderId)  from order_table");

        DataStream<Row> orderDataStream = tableEnv.toAppendStream(query, Row.class);
        orderDataStream.print();

        env.execute("sql_udf");
    }

    public static class HashCode extends ScalarFunction {

        public int eval(String s) {
            return s.hashCode();
        }
    }

}
