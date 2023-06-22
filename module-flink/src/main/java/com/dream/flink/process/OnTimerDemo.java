package com.dream.flink.process;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

public class OnTimerDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        conf.setString("execution.checkpointing.interval", "10s");
        conf.setString("execution.savepoint.path", "file:///tmp/flinkjob/980ce11849d28ffc144fa6dac1d98083/chk-2");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        env.setParallelism(1);

        EnvironmentSettings envSetting = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();

        StreamTableEnvironment tableEnv =  StreamTableEnvironment.create(env, envSetting);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  id           INT,\n" +
                "  app          INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='1',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='10',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery("select * from orders");
        DataStream<Row> rowDataStream = tableEnv.toAppendStream(query, Row.class);

        TypeInformation<?>[] returnTypes = new TypeInformation[4];
        returnTypes[0] = Types.INT;
        returnTypes[1] = Types.INT;
        returnTypes[2] = Types.LONG;
        returnTypes[3] = Types.INT;


        rowDataStream.keyBy(new KeySelector<Row, String>() {
            @Override
            public String getKey(Row value) throws Exception {
                return value.getFieldAs(2);
            }
        }).process(new KeyedProcessFunction<String, Row, Row>() {

            private Row firstRow;

            @Override
            public void processElement(Row value, Context ctx, Collector<Row> out) throws Exception {
//                out.collect(value);
                if (firstRow == null) {
                    firstRow = value;
                }
                ctx.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 3000);
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Row> out) throws Exception {
                // TODO restore 时调用的 onTimer 会有 bug，异常时，任务不会退出。
                Row colRow = new Row(4);
                colRow.setField(0, 0);
                colRow.setField(1, 1);
                colRow.setField(2, 2);
                colRow.setField(3, 3);
                out.collect(colRow);
//                throw new RuntimeException("xxxx");
            }
        })
        .returns(new RowTypeInfo(returnTypes))
        .print();

        env.execute(OnTimerDemo.class.getSimpleName());
    }

}
