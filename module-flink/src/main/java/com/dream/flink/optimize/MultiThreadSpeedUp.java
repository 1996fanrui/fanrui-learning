package com.dream.flink.optimize;

import com.dream.flink.sql.FlinkSqlUtil;
import com.dream.flink.sql.profile.BackPressureDemo;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.concurrent.*;

public class MultiThreadSpeedUp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  id           INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='1',\n" +
                "   'fields.user_id.length'='100'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery("select * from orders");
        tableEnv.toAppendStream(query, Row.class)
//                .flatMap(new SlowFlatMapFunction())
                .flatMap(new FastFlatMapFunction())
                .print();


        env.execute(BackPressureDemo.class.getSimpleName());
    }

    private static class SlowFlatMapFunction extends RichFlatMapFunction<Row, String> {

        @Override
        public void flatMap(Row row, Collector<String> out) throws Exception {
            int id = row.getFieldAs(0);
            String userId = row.getFieldAs(1);
            String[] words = userId.split("1");

            for (String word : words) {
                String result = id + heavyOperation(word);
                out.collect(result);
            }
        }
    }

    private static class FastFlatMapFunction extends RichFlatMapFunction<Row, String> {
        // This ExecutorService can be static, but this CompletionService must be created in each function.
        // The static ExecutorService can make multiple slots share the same thread pool.
        ExecutorService executor;
        CompletionService<String> cs;

        @Override
        public void open(Configuration parameters) {
            this.executor = Executors.newFixedThreadPool(20);
            this.cs = new ExecutorCompletionService<>(executor);
        }

        @Override
        public void flatMap(Row row, Collector<String> out) throws Exception {
            int id = row.getFieldAs(0);
            String userId = row.getFieldAs(1);
            String[] words = userId.split("1");

            for (String word : words) {
                cs.submit(() -> id + heavyOperation(word));
            }
            for (int i = 0; i < words.length; i++) {
                out.collect(cs.take().get());
            }
        }

        @Override
        public void close() {
            executor.shutdown();
        }
    }

    private static String heavyOperation(String input) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
        return input;
    }
}
