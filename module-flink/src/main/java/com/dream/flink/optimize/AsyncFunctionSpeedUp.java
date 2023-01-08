package com.dream.flink.optimize;

import com.dream.flink.sql.FlinkSqlUtil;
import com.dream.flink.sql.profile.BackPressureDemo;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.*;

public class AsyncFunctionSpeedUp {

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
                "   'fields.id.kind'='sequence',\n" +
                "   'fields.id.start'='0',\n" +
                "   'fields.id.end'='100',\n" +
                "   'rows-per-second'='10',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery("select * from orders");
        DataStream<Row> rowDataStream = tableEnv.toAppendStream(query, Row.class);

        // sync io
//        rowDataStream.map(new SlowMapFunction()).print();

        // ordered async io
        AsyncDataStream.orderedWait(rowDataStream, new FastAsyncFunction(), 2, TimeUnit.SECONDS).print();

        // unordered async io
//        AsyncDataStream.unorderedWait(rowDataStream, new FastAsyncFunction(), 2, TimeUnit.SECONDS).print();

        env.execute(BackPressureDemo.class.getSimpleName());
    }

    private static class SlowMapFunction extends RichMapFunction<Row, String> {

        @Override
        public String map(Row row) throws Exception {
            int id = row.getFieldAs(0);
            return heavyOperation(Integer.toString(id));
        }
    }

    private static class FastAsyncFunction extends RichAsyncFunction<Row, String> {

        ExecutorService executor;

        @Override
        public void open(Configuration parameters) {
            this.executor = Executors.newFixedThreadPool(5);
        }

        @Override
        public void asyncInvoke(Row row, ResultFuture<String> resultFuture) {
            CompletableFuture.supplyAsync(
                    () -> {
                        int id = row.getFieldAs(0);
                        return heavyOperation(Integer.toString(id));
                    }, executor)
                    // 无论是否异常，whenCompleteAsync 一定会被调用。
                    // 当 throwable 为 null，表示没有异常，从 result 里拿结果即可
                    // 当 throwable 不为 null，表示有异常，则处理异常
                    .whenCompleteAsync((result, throwable) -> {
                        if (result != null) {
                            resultFuture.complete(Collections.singleton(result));
                        } else {
                            // 如果想继续往外抛异常，可调用 completeExceptionally，
                            // 如果不往外抛异常，可以调用 complete
//                        resultFuture.completeExceptionally(throwable);
                            resultFuture.complete(Collections.emptyList());
                        }
                    }, executor);
        }

        @Override
        public void timeout(Row input, ResultFuture<String> resultFuture) {
            System.out.printf("%s timeout%n", input);
        }

        @Override
        public void close() throws Exception {
            executor.shutdown();
        }
    }


    private static String heavyOperation(String input) {
        Random random = new Random();
        try {
            int sleep = random.nextInt(1000);
            TimeUnit.MILLISECONDS.sleep(sleep);
        } catch (InterruptedException ignored) {
        }
        return input;
    }
}
