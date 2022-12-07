package com.dream.flink.process;

import org.apache.flink.api.java.functions.KeySelector;
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
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        EnvironmentSettings envSetting = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
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

        rowDataStream.keyBy(new KeySelector<Row, String>() {
            @Override
            public String getKey(Row value) throws Exception {
                return value.getFieldAs(2);
            }
        }).process(new KeyedProcessFunction<String, Row, Row>() {

            private Row firstRow;

            @Override
            public void processElement(Row value, Context ctx, Collector<Row> out) throws Exception {
                out.collect(value);
                if (firstRow == null) {
                    firstRow = value;
                }
                ctx.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 6000);
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Row> out) throws Exception {
                System.out.println(timestamp);
                Row colRow = new Row(firstRow.getArity() + 2);
                for (int i = 0; i < firstRow.getArity(); i++) {
                    colRow.setField(i, firstRow.getField(i));
                }
                colRow.setField(firstRow.getArity(), 2);
                colRow.setField(firstRow.getArity()+1, 2);
                out.collect(colRow);
//                throw new RuntimeException("xxxx");
            }
        }).print();

        env.execute(OnTimerDemo.class.getSimpleName());
    }

}
