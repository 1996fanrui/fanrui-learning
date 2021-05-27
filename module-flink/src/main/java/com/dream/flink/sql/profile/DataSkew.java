package com.dream.flink.sql.profile;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.concurrent.TimeUnit;

/**
 * @author fanrui03
 * @date 2021/5/28 00:27
 *
 * SQL 优化：https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/table/tuning/
 */
public class DataSkew {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

//        // access flink configuration
//        Configuration configuration = tableEnv.getConfig().getConfiguration();
//        // set low-level key-value options
//        configuration.setString("table.exec.mini-batch.enabled", "true"); // local-global aggregation depends on mini-batch is enabled
//        configuration.setString("table.exec.mini-batch.allow-latency", "5 s");
//        configuration.setString("table.exec.mini-batch.size", "5000");
//        configuration.setString("table.optimizer.agg-phase-strategy", "TWO_PHASE");

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='10000',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='80',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery(
                "select aaa\n" +
                        "   ,count(*) as pv\n" +
                        "   ,count(distinct user_id) as uv\n" +
                        "from \n" +
                        "   (\n" +
                        "       select case when app < 50 then 1 else app end as aaa\n" +
                        "           ,user_id\n" +
                        "       from orders\n" +
                        "   ) a \n" +
                        "group by aaa");

        tableEnv.toRetractStream(query, Row.class)
                .addSink(new RichSinkFunction<Tuple2<Boolean, Row>>() {
                    @Override
                    public void invoke(Tuple2<Boolean, Row> value, Context context) throws Exception {
                        TimeUnit.MILLISECONDS.sleep(10);
                        System.out.println(value);
                    }
                })
                .name("MySink");

        System.out.println(env.getExecutionPlan());

        env.execute(DataSkew.class.getSimpleName());
    }

}
