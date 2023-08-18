package com.dream.flink.checkpoint.metadata;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CheckpointFailureDemo {

    private static final Logger LOG = LoggerFactory.getLogger(CheckpointFailureDemo.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = getEnv();

        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(1000),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")
                .map((MapFunction<Long, Long>) value -> value).name("Map___1")
                .rebalance()
                .addSink(new MySink<>())
                .name("MySink");

        env.execute(CheckpointFailureDemo.class.getSimpleName());
    }

    public static class MySink<T> extends RichSinkFunction<T> implements CheckpointedFunction {

        @Override
        public void invoke(T value, Context context) throws Exception {
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) {
            if (getRuntimeContext().getIndexOfThisSubtask() == 0) {
                long checkpointId = context.getCheckpointId();
                if(checkpointId == 3) {
                    throw new RuntimeException("jsodfkjsklfklj");
                }
            }
        }

        @Override
        public void initializeState(FunctionInitializationContext context) {
        }
    }

    private static StreamExecutionEnvironment getEnv() {
        String OSType = System.getProperty("os.name");
        LOG.info("start job on {}", OSType);

        Configuration conf = new Configuration();
        StreamExecutionEnvironment env = OSType.startsWith("Mac OS") ? getIdeaEnv(conf) : getProdEnv(conf);
        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(15), CheckpointingMode.EXACTLY_ONCE);
        return env;
    }

    private static StreamExecutionEnvironment getProdEnv(Configuration conf) {
        return StreamExecutionEnvironment.getExecutionEnvironment(conf);
    }

    private static StreamExecutionEnvironment getIdeaEnv(Configuration conf) {
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(5);
        return env;
    }

}
