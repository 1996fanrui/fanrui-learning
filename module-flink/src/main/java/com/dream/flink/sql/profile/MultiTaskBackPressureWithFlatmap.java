package com.dream.flink.sql.profile;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author fanrui
 * @date 2022-03-10 18:54:57
 */
public class MultiTaskBackPressureWithFlatmap {

    private static final Logger LOG = LoggerFactory.getLogger(MultiTaskBackPressureWithFlatmap.class);

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int sleepMs = parameterTool.getInt("sleepMs", 10);

        StreamExecutionEnvironment env = getEnv();

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='100000000',\n" +
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
                .flatMap(new FlatMapFunction<Row, Row>() {
                    @Override
                    public void flatMap(Row value, Collector<Row> collector) throws Exception {
                        for (int i = 0; i < 5; i++) {
                            collector.collect(value);
                        }
                    }
                })
                .name("FlatMap___1")
                .rebalance()
                .map((MapFunction<Row, Row>) value -> value).name("Map___2")
                .rebalance()
                .map((MapFunction<Row, Row>) value -> value).name("Map___3")
                .rebalance()
                .addSink(new RichSinkFunction<Row>() {
                    @Override
                    public void invoke(Row value, Context context) throws InterruptedException {
                    // sleep cause backpressure. By default, sleepMs=10ms
                    TimeUnit.MILLISECONDS.sleep(sleepMs);
                    }
                })
                .name("MySink");

        env.execute(MultiTaskBackPressureWithFlatmap.class.getSimpleName());
    }

    private static StreamExecutionEnvironment getEnv() {
        String OSType = System.getProperty("os.name");
        LOG.info("start job on {}", OSType);

        Configuration conf = new Configuration();
        conf.setString("execution.checkpointing.unaligned", "true");
        conf.setString("execution.checkpointing.alignment-timeout", "0ms");
        conf.setString("rest.flamegraph.enabled", "true");
        conf.setString("state.backend", "hashmap");

        StreamExecutionEnvironment env = OSType.startsWith("Mac OS") ? getIdeaEnv(conf) : getProdEnv(conf);
        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(30), CheckpointingMode.EXACTLY_ONCE);
        return env;
    }

    private static StreamExecutionEnvironment getProdEnv(Configuration conf) {
        return StreamExecutionEnvironment.getExecutionEnvironment(conf);
    }

    private static StreamExecutionEnvironment getIdeaEnv(Configuration conf) {
        conf.set(RestOptions.PORT, 34567);
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(20);
        return env;
    }

}
