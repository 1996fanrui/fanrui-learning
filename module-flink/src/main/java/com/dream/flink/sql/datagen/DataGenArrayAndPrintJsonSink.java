package com.dream.flink.sql.datagen;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class DataGenArrayAndPrintJsonSink {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        String sourceDDL = "CREATE TABLE kafka_datagen (\n" +
                "    a array<row<\n" +
                "        batch_no bigint\n" +
                "        ,spu string>\n" +
                "    >\n" +
                ")\n" +
                "WITH (\n" +
                "  'connector' = 'datagen'\n" +
                "  ,'fields.a.element.batch_no.max' = '100'\n" +
                "  ,'fields.a.kind' = 'random'\n" +
                "  ,'fields.a.element.batch_no.min' = '1'\n" +
                "  ,'fields.a.element.spu.length' = '3'\n" +
                "  ,'rows-per-second' = '3'\n" +
                ")\n";

        tableEnv.executeSql(sourceDDL);

        String sinkDDL = "create table print_sink ( \n" +
                "a array<row<\n" +
                "        batch_no bigint\n" +
                "        ,spu string\n" +
                "    >>\n" +
                ") with (" +
                "'connector' = 'print' " +
                ")";
        tableEnv.executeSql(sinkDDL);

        String dml = "insert into print_sink\n" +
                "select  *\n" +
                "from    kafka_datagen\n";

        tableEnv.executeSql(dml);
    }

}
