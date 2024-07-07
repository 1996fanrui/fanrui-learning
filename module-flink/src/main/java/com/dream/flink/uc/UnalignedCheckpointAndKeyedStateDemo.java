package com.dream.flink.uc;

import com.dream.flink.optimize.deduplication.KeyedStateDeduplication;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.concurrent.TimeUnit;

/**
 * @author fanrui
 * @date 2024-07-07 13:30:39
 * <p>
 * bin/flink run -c com.dream.flink.uc.UnalignedCheckpointAndKeyedStateDemo /Users/fanrui/code/github/fanrui-learning/module-flink/target/module-flink-1.0-SNAPSHOT.jar
 */
public class UnalignedCheckpointAndKeyedStateDemo {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int sleepMs = parameterTool.getInt("sleepMs", 1);

        StreamExecutionEnvironment env = getEnv();
        env.setParallelism(6);

        DataStreamSource<Long> streamSource = env.fromSequence(0, 1_000_000_000_000_000L);

        streamSource
                .map((MapFunction<Long, Long>) value -> value).name("Map___1")
                .rebalance()
                .map((MapFunction<Long, Long>) value -> value).name("Map___2")
                .rebalance()
                .map((MapFunction<Long, Long>) value -> value).name("Map___3")
                .rebalance()
                .keyBy(new KeySelector<Long, String>() {
                    @Override
                    public String getKey(Long value) throws Exception {
                        return value + "sklfjalorwejknsncnjkdjhfklajlkjklajfdlkjweoruwofjkasnnvnjhjafhajgjoaogja";
                    }
                })
                .filter(new KeyedStateDeduplication.DeduplicationFilter<>())
                .rebalance()
                .addSink(new RichSinkFunction<Long>() {
                    @Override
                    public void invoke(Long value, Context context) throws InterruptedException {
                        if (getRuntimeContext().getIndexOfThisSubtask() == 0) {
                            // sleep cause backpressure
                            TimeUnit.MILLISECONDS.sleep(sleepMs);
                        }
                    }
                })
                .name("MySink");

        env.execute(UnalignedCheckpointAndKeyedStateDemo.class.getSimpleName());
    }

    private static StreamExecutionEnvironment getEnv() {
        Configuration conf = new Configuration();
        conf.setString("execution.checkpointing.unaligned", "true");
        conf.setString("rest.flamegraph.enabled", "true");
        conf.setString("state.backend", "rocksdb");
        conf.setString("execution.checkpointing.incremental", "true");
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        conf.setString("execution.checkpointing.file-merging.enabled", "true");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(30), CheckpointingMode.EXACTLY_ONCE);
        return env;
    }

}
