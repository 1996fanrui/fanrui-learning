package com.dream.datalake.paimon.quickstart;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.concurrent.TimeUnit;

public class QuickStartWriteDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        conf.set(RestOptions.PORT, 34567);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(10));
        env.setParallelism(2);

        // Don't use the catalog to prevent the table is exist.
//        String catalogDDL = "CREATE CATALOG my_catalog WITH (\n" +
//                "    'type'='paimon',\n" +
//                "    'warehouse'='file:/tmp/paimon'\n" +
//                ")";
//
//        String useCatalog = "USE CATALOG my_catalog";

        String paimonDDL = "CREATE TABLE word_count (\n" +
                "    word STRING PRIMARY KEY NOT ENFORCED,\n" +
                "    cnt BIGINT\n" +
                ") PARTITIONED BY (word) with (\n" +
                "    'connector' = 'paimon',\n" +
                "    'path' = 'file:/tmp/paimon/default.db/word_count1',\n" +
                "    'auto-create' = 'true'\n" +
                ")";

        String sourceDDL = "CREATE TEMPORARY TABLE word_table (\n" +
                "    word STRING\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'fields.word.length' = '1'\n" +
                ")";

        String insertSQL = "INSERT INTO word_count SELECT word, COUNT(*) FROM word_table GROUP BY word\n";

        tableEnv.executeSql(paimonDDL);
        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(insertSQL);
    }

}
