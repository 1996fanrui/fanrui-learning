package com.dream.flink.kafka.alignment;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Preconditions;

import java.time.Duration;

public class KafkaAlignmentDemo {

    public static final String SLOW_SOURCE_NAME = "SlowNumberSequenceSource";
    public static final String FAST_SOURCE_NAME = "FastNumberSequenceSource";
    private static final Duration UPDATE_INTERVAL = Duration.ofMillis(1000);
    public static final Duration MAX_DRIFT = Duration.ofMillis(1000);

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
//        conf.setString("execution.savepoint.path", "file:///tmp/flinkjob/8cd55f071073f34531856eb4420a5cd1/chk-24");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.setParallelism(2);
        env.enableCheckpointing(10000);

        env.fromSource(KafkaSource.<SamplePojo>builder()
                        .setBootstrapServers("localhost:9092")
                        .setTopics("slow-topic")
                        .setGroupId("test")
                        .setStartingOffsets(OffsetsInitializer.latest())
                        .setValueOnlyDeserializer(new JsonDeserializationSchema<>(SamplePojo.class))
                        .build(),
                WatermarkStrategy.forGenerator(ctx -> new PunctuatedGenerator())
                        .withWatermarkAlignment(
                                "group-1",
                                MAX_DRIFT,
                                UPDATE_INTERVAL)
                        .withTimestampAssigner((r, t) -> r.getTs()),
                SLOW_SOURCE_NAME).print();

        env.fromSource(KafkaSource.<SamplePojo>builder()
                        .setBootstrapServers("localhost:9092")
                        .setTopics("fast-topic")
                        .setGroupId("test")
                        .setStartingOffsets(OffsetsInitializer.latest())
                        .setValueOnlyDeserializer(new JsonDeserializationSchema<>(SamplePojo.class))
                        .build(),
                WatermarkStrategy.forGenerator(ctx -> new PunctuatedGenerator())
                        .withWatermarkAlignment(
                                "group-1",
                                MAX_DRIFT,
                                UPDATE_INTERVAL)
                        .withTimestampAssigner((r, t) -> r.getTs()),
                FAST_SOURCE_NAME).addSink(new MySink<>());

        env.execute();
    }

    private static class PunctuatedGenerator implements WatermarkGenerator<SamplePojo> {
        @Override
        public void onEvent(SamplePojo event, long eventTimestamp, WatermarkOutput output) {
            Preconditions.checkState(event.getTs() == eventTimestamp);
            output.emitWatermark(new Watermark(eventTimestamp));
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
        }
    }

    public static class MySink<T> extends RichSinkFunction<T> implements CheckpointedFunction {

        private static final long DEFAULT_CHECKPOINT_ID = -1;

        private long firstCheckpointId = DEFAULT_CHECKPOINT_ID;

        @Override
        public void invoke(T value, Context context) throws Exception {
            System.out.println(value);
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) {
            long currentCheckpointId = context.getCheckpointId();
            if (firstCheckpointId == DEFAULT_CHECKPOINT_ID) {
                firstCheckpointId = currentCheckpointId;
            }
            if (getRuntimeContext().getIndexOfThisSubtask() == 0) {
                if (currentCheckpointId > firstCheckpointId + 6) {
                    throw new RuntimeException("jsodfkjsklfklj");
                }
            }
        }

        @Override
        public void initializeState(FunctionInitializationContext context) {
        }
    }

}
