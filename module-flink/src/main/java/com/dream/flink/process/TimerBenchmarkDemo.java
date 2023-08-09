package com.dream.flink.process;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

public class TimerBenchmarkDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        conf.setString("execution.checkpointing.interval", "100s");
        conf.setString("rest.flamegraph.enabled", "true");
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
                "   'rows-per-second'='1000000000',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='100000000',\n" +
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


        rowDataStream.keyBy(new KeySelector<Row, Integer>() {
            @Override
            public Integer getKey(Row value) {
                return value.getFieldAs(1);
            }
        }).process(new KeyedProcessFunction<Integer, Row, Row>() {

            private Row sentRow;

            @Override
            public void open(Configuration parameters) {
                sentRow = new Row(4);
                sentRow.setField(0, 0);
                sentRow.setField(1, 1);
                sentRow.setField(2, 2L);
                sentRow.setField(3, 3);
            }

            @Override
            public void processElement(Row value, Context ctx, Collector<Row> out) {
                ctx.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 1000);
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Row> out) {
                out.collect(sentRow);
            }
        })
        .returns(new RowTypeInfo(returnTypes))
        .addSink(new DiscardingSink<>());

        env.execute(TimerBenchmarkDemo.class.getSimpleName());
    }
}
