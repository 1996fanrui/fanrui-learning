package com.dream.datalake.paimon.lookup;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.concurrent.TimeUnit;

public class PaimonLookupJoinDataGenerator {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        conf.set(RestOptions.PORT, 34567);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(20));
        env.setParallelism(2);

        String paimonDDL = "CREATE TABLE city_info (\n" +
                "  city_id INT PRIMARY KEY NOT ENFORCED,\n" +
                "  city_name STRING\n" +
                ") WITH (\n" +
                "    'connector' = 'paimon',\n" +
                "    'path' = 'file:/tmp/paimon/default.db/city_info',\n" +
                "    'auto-create' = 'true'\n" +
                ")";

        String sourceDDL = "CREATE TEMPORARY TABLE word_table (\n" +
                "    city_id INT,\n" +
                "    city_name STRING\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'rows-per-second'='1000000',\n" +
                "    'fields.city_id.min'='1',\n" +
                "    'fields.city_id.max'='100',\n" +
                "    'fields.city_name.length' = '5',\n" +
                "    'fields.city_name.length' = '5'\n" +
                ")";

        String insertSQL = "INSERT INTO city_info SELECT * FROM word_table\n";

        tableEnv.executeSql(paimonDDL);
        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(insertSQL);
    }

}
