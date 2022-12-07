package com.dream.flink.leak;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ThreadLeak {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='1000000',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='10',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery("select * from orders");
        tableEnv.toAppendStream(query, Row.class)
                .rebalance()
                .addSink(new ThreadLeakSink())
                .name("MySink");

        env.execute(ThreadLeak.class.getSimpleName());
    }

    private static class ThreadLeakSink extends RichSinkFunction<Row> {

        List<Thread> threadList = new ArrayList<>();

        private ThreadLeakSink() {
        }

        @Override
        public void invoke(Row value, Context context) throws InterruptedException {
            if (getRuntimeContext().getIndexOfThisSubtask() == 0) {
                Thread thread = new Thread(() -> {
                    while (true) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                });
                threadList.add(thread);
                thread.start();
            }
            TimeUnit.MILLISECONDS.sleep(50);
        }

        @Override
        public void close() {
            for (Thread thread : threadList) {
                thread.interrupt();
            }
        }
    }
}
