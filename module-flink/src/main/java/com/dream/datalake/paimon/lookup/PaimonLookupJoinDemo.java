package com.dream.datalake.paimon.lookup;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * 相关实现可以参考 {@link org.apache.paimon.flink.lookup.FileStoreLookupFunction}
 *
 * 原理简述：全量数据写到 TM 本地的 rocksdb 中，每次 join 时，从 rocksdb 中查询。
 * 按照 continuous.discovery-interval 周期，定期从 Paimon 中读取增量数据，写入到 RocksDB 中。
 */
public class PaimonLookupJoinDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        conf.set(RestOptions.PORT, 34568);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(30));
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
                "    proc_time AS PROCTIME()" +
                ") WITH (\n" +
                "    'connector' = 'datagen',\n" +
                "    'rows-per-second'='1',\n" +
                "    'fields.city_id.min'='1',\n" +
                "    'fields.city_id.max'='100'\n" +
                ")";

        String printDDL = "CREATE TEMPORARY TABLE print_table (\n" +
                "    city_id INT,\n" +
                "    proc_time TIMESTAMP_LTZ(3),\n" +
                "    city_name STRING\n" +
                ") WITH (\n" +
                "    'connector' = 'print'\n" +
                ")";

        String insertSQL = "INSERT INTO print_table SELECT o.city_id, proc_time, city_name " +
                "FROM word_table as o\n" +
                "join city_info\n" +
                "FOR SYSTEM_TIME AS OF o.proc_time AS c\n " +
                "on o.city_id = c.city_id ";

        tableEnv.executeSql(paimonDDL);
        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(printDDL);
        tableEnv.executeSql(insertSQL);
    }

}
