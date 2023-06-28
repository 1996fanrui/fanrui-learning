package com.dream.datalake.paimon.quickstart;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.concurrent.TimeUnit;

public class QuickStartReadDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
//        conf.setString("execution.savepoint.path", "file:///tmp/flinkjob/d043c87439e832ccd1cadbf7ab5c6fe2/chk-43");
        conf.set(RestOptions.PORT, 34568);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(30));
        env.setParallelism(1);

        String paimonDDL = "CREATE TABLE word_count (\n" +
                "    word STRING PRIMARY KEY NOT ENFORCED,\n" +
                "    cnt BIGINT\n" +
                ") with (\n" +
                "    'connector' = 'paimon',\n" +
                "    'path' = 'file:/tmp/paimon/default.db/word_count',\n" +
                "    'auto-create' = 'true'\n" +
                ")";

        String printDDL = "CREATE TEMPORARY TABLE print_table (\n" +
                "    word STRING,\n" +
                "    cnt BIGINT\n" +
                ") WITH (\n" +
                "    'connector' = 'print'\n" +
                ")";

        String insertSQL = "INSERT INTO print_table SELECT * FROM word_count "
//               + "/*+ OPTIONS('consumer-id' = 'myid') */"
                ;

        tableEnv.executeSql(paimonDDL);
        tableEnv.executeSql(printDDL);
        tableEnv.executeSql(insertSQL);
    }

}
