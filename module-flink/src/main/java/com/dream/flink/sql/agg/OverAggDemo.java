package com.dream.flink.sql.agg;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class OverAggDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  id           INT,\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts  - INTERVAL '1' SECOND \n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='1',\n" +
                "   'fields.id.kind'='sequence',\n" +
                "   'fields.id.start'='0',\n" +
                "   'fields.id.end'='1000000',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='2',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        String sinkDDL = "create table print_sink ( \n" +
                "  app          INT,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  channel           INT,\n" +
                "  max_amount           INT,\n" +
                "  min_amount           INT\n" +
                ") with ('connector' = 'print' )";
        tableEnv.executeSql(sinkDDL);

        String dml = "insert into print_sink\n" +
                "SELECT app, ts, \n" +
                " last_value(channel) OVER w as channel,\n" +
                "  max(channel) OVER w AS max_amount,\n" +
                "  min(channel) OVER w AS min_amount\n" +
                "FROM orders\n" +
                "WINDOW w AS (\n" +
                "  PARTITION BY app\n" +
                "  ORDER BY ts\n" +
                "  ROWS BETWEEN 5 PRECEDING AND CURRENT ROW\n)";

        tableEnv.executeSql(dml);

    }

}
