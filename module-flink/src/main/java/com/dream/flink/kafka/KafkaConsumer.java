package com.dream.flink.kafka;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class KafkaConsumer {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String printDDL = "CREATE TABLE print_table (\n" +
                "  app          INT,\n" +
                "  city_id      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "   'connector' = 'print'\n" +
                ")";

        String kafkaDDL = "create table kafka_orders\n" +
                "(\n" +
                "  app          INT,\n" +
                "  city_id      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts TIMESTAMP(3)\n" +
                ") \n" +
                "with\n" +
                "(\n" +
                "    'connector' = 'kafka',\n" +
                "    'topic' = 'quickstart-events',\n" +
                "    'properties.bootstrap.servers' = 'localhost:9092',\n" +
                "    'properties.group.id' = 'test',\n" +
                "    'scan.startup.mode' = 'latest-offset',\n" +
                "    'format' = 'json'" +
                ")";

        String insertSQL = "insert into print_table\n" +
                "select  *\n" +
                "from kafka_orders";

        System.out.println(printDDL);
        System.out.println(kafkaDDL);
        System.out.println(insertSQL);

        tableEnv.executeSql(printDDL);
        tableEnv.executeSql(kafkaDDL);
        tableEnv.executeSql(insertSQL);
    }
}
