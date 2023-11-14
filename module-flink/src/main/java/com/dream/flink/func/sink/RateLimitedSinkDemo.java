package com.dream.flink.func.sink;

import com.dream.flink.sql.profile.BackPressureDemo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava31.com.google.common.util.concurrent.RateLimiter;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class RateLimitedSinkDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        EnvironmentSettings envSetting = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, envSetting);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  id           INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='100000',\n" +
                "   'fields.user_id.length'='5'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery("select * from orders");
        tableEnv.toAppendStream(query, Row.class)
                .addSink(new LimitedSink(2));

        env.execute(BackPressureDemo.class.getSimpleName());
    }

    static class LimitedSink extends RichSinkFunction<Row> {
        private final double permitsPerSecond;
        private RateLimiter rateLimiter;

        LimitedSink(double permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            this.rateLimiter = RateLimiter.create(permitsPerSecond);
        }

        @Override
        public void invoke(Row value, Context context) throws Exception {
            // limiter
            rateLimiter.acquire();
            System.out.println(value);
        }
    }

}
