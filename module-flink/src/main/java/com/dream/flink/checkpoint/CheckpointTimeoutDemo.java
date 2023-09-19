package com.dream.flink.checkpoint;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.concurrent.TimeUnit;

public class CheckpointTimeoutDemo {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int checkpointSleepSecond = parameterTool.getInt("checkpointSleepSecond", 650);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(1000),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")
                .addSink(new MySink<>(checkpointSleepSecond))
                .name("MySink");

        env.execute(CheckpointTimeoutDemo.class.getSimpleName());
    }

    public static class MySink<T> extends RichSinkFunction<T> implements CheckpointedFunction {

        private final int checkpointSleepSecond;

        public MySink(int checkpointSleepSecond) {
            this.checkpointSleepSecond = checkpointSleepSecond;
        }

        @Override
        public void invoke(T value, Context context) throws Exception {
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws InterruptedException {
            TimeUnit.SECONDS.sleep(checkpointSleepSecond);
        }

        @Override
        public void initializeState(FunctionInitializationContext context) {
        }
    }
}
