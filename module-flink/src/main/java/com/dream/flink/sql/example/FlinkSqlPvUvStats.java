package com.dream.flink.sql.example;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * @author fanrui
 * @time 2020-04-10 21:35:49
 * 使用 Flink SQL 按照订单信息统计各个城市的 pv/uv 信息
 */
public class FlinkSqlPvUvStats {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        EnvironmentSettings blinkStreamSettings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamTableEnvironment blinkStreamTableEnv = StreamTableEnvironment.create(env, blinkStreamSettings);

        String ddlSource = "CREATE TABLE order_stream (\n" +
                "    orderId STRING,\n" +
                "    userId STRING,\n" +
                "    goodsId INT,\n" +
                "    price BIGINT,\n" +
                "    cityId INT,\n" +
                "    `time` BIGINT\n" +
                ") WITH (\n" +
                "    'connector.type' = 'kafka',\n" +
                "    'connector.version' = '0.10',\n" +
                "    'connector.topic' = 'order',\n" +
                "    'connector.properties.group.id' = 'flink-sql',\n" +
                "    'connector.startup-mode' = 'earliest-offset',\n" + // "earliest-offset", "latest-offset", "group-offsets","specific-offsets"
                "    'connector.properties.zookeeper.connect' = 'test-node1.zk.bigdata.dmp.com:2181/kafka',\n" +
                "    'connector.properties.bootstrap.servers' = 'test-kafka1.hadoop.bigdata.dmp.com:9092,test-kafka2.hadoop.bigdata.dmp.com:9092,test-kafka3.hadoop.bigdata.dmp.com:9092',\n" +
                "    'format.type' = 'json'\n" +
                ")";

        // 计算 每个城市对应的 订单数
        String countSql = "select cityId, count(orderId) from order_stream group by cityId";

        blinkStreamTableEnv.sqlUpdate(ddlSource);
        Table countTable = blinkStreamTableEnv.sqlQuery(countSql);
        blinkStreamTableEnv.toRetractStream(countTable, Row.class).print();

        // 计算每个城市对应的 uv
        String distinctSql = "select cityId, count(distinct userId) from order_stream group by cityId";
        Table distinctTable = blinkStreamTableEnv.sqlQuery(distinctSql);
        blinkStreamTableEnv.toRetractStream(distinctTable, Row.class).print("==");

        blinkStreamTableEnv.execute("Blink Stream SQL count/uv demo");
    }
}
