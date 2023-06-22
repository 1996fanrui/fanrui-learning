package com.dream.flink.network;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author fanrui
 * @date 2022-03-10 18:54:57
 */
public class DynamicRebalanceDemo {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicRebalanceDemo.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = getEnv();

        DataStreamSource<Long> streamSource = env.fromSequence(0, 1_000_000_000_000_000L);

        streamSource
                .map((MapFunction<Long, Long>) value -> value).name("Map___1")
                .rebalance()
                .map((MapFunction<Long, Long>) value -> value).name("Map___2")
                .rebalance()
                .map(new SlowSubtaskMap()).name("Map___3")
                .rebalance()
                .addSink(new DiscardingSink<>());

        env.execute(DynamicRebalanceDemo.class.getSimpleName());
    }

    private static class SlowSubtaskMap extends RichMapFunction<Long, Long> {
        @Override
        public Long map(Long value) throws Exception {
            int ind = getRuntimeContext().getIndexOfThisSubtask();
            if (ind == 0) {
                Thread.sleep(100);
            }
            return value;
        }
    }

    private static StreamExecutionEnvironment getEnv() {
        String OSType = System.getProperty("os.name");
        LOG.info("start job on {}", OSType);

        Configuration conf = new Configuration();
        conf.setString("execution.checkpointing.tolerable-failed-checkpoints", "100");

        StreamExecutionEnvironment env = OSType.startsWith("Mac OS") ? getIdeaEnv(conf) : getProdEnv(conf);
//        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(30), CheckpointingMode.EXACTLY_ONCE);
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
