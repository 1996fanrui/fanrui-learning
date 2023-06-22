package com.dream.flink.kafka;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * kafka-topics --create --topic quickstart-events --bootstrap-server localhost:9092
 * kafka-topics --bootstrap-server localhost:9092 --alter --topic quickstart-events --partitions 2
 * kafka-topics --bootstrap-server localhost:9092 --describe --topic quickstart-events
 */
public class KafkaDataGenerator {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  city_id      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts AS PROCTIME()\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='10',\n" +
                "   'fields.app.min'='100',\n" +
                "   'fields.app.max'='200',\n" +
                "   'fields.city_id.min'='1',\n" +
                "   'fields.city_id.max'='10',\n" +
                "   'fields.user_id.length'='10'\n" +
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
                "    'format' = 'json'\n" +
                ")";

        String insertSQL = "insert into kafka_orders\n" +
                "select  *\n" +
                "from orders";

        System.out.println(sourceDDL);
        System.out.println(kafkaDDL);
        System.out.println(insertSQL);

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(kafkaDDL);
        tableEnv.executeSql(insertSQL);
    }
}
