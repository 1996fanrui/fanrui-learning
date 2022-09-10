package com.dream.flink.sql.datagen;

import com.dream.flink.sql.FlinkSqlUtil;
import com.dream.flink.sql.profile.sink.ComplexSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * @author fanrui03
 * @date 2021/5/29 13:43
 */
public class DataGenAndPrintSink {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  id           INT,\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='2',\n" +
//                "   'fields.id.kind'='sequence',\n" +
//                "   'fields.id.start'='0',\n" +
//                "   'fields.id.end'='100',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='10',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        String sinkDDL = "create table print_sink ( \n" +
                "  id           INT,\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3)\n" +
                ") with ('connector' = 'print' )";
        tableEnv.executeSql(sinkDDL);

        String dml = "insert into print_sink\n" +
                "select id" +
                "       ,app" +
                "       ,channel" +
                "       ,user_id" +
                "       ,ts" +
                "   from orders";

        tableEnv.executeSql(dml);
    }

}
