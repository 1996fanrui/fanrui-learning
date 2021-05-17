package com.dream.flink.sql.profile;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.concurrent.TimeUnit;

/**
 * @author fanrui03
 * @date 2021/5/15 14:22
 */
public class BackPressureDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts AS localtimestamp,\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='10000',\n" +
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
                .addSink(new MySink()).name("MySink");

        System.out.println(env.getExecutionPlan());

        env.execute(BackPressureDemo.class.getSimpleName());
    }

    private static class MySink extends RichSinkFunction<Row> implements CheckpointedFunction {

        private boolean needSleep;

        public MySink() {
            System.out.println("MySink 构造器");
            this.needSleep = false;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            System.out.println("MySink open");
            int indexOfThisSubtask = getRuntimeContext().getIndexOfThisSubtask();
            if (indexOfThisSubtask == 1) {
                needSleep = true;
            } else {
                needSleep = false;
            }
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            System.out.println("snapshotState checkpoint id:" + context.getCheckpointId());
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            System.out.println("initializeState .");
        }

        @Override
        public void invoke(Row value, Context context) throws Exception {
            if (needSleep) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            System.out.println(value);
        }
    }

}
