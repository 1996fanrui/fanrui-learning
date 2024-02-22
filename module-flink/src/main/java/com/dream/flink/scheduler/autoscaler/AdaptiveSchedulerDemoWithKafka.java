package com.dream.flink.scheduler.autoscaler;

import com.dream.flink.func.map.RateLimiterSleepMapFunction;
import com.dream.flink.kafka.demo.KafkaDataGenerator;
import com.dream.flink.kafka.demo.SamplePojo;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

import java.util.concurrent.TimeUnit;

/**
 * Data from {@link KafkaDataGenerator}
 */
public class AdaptiveSchedulerDemoWithKafka {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("taskmanager.numberOfTaskSlots", "100");
        conf.setString("rest.flamegraph.enabled", "true");

        // Enable the adaptive scheduler and autoscaler.
        conf.setString("jobmanager.scheduler", "adaptive");
        conf.setString("job.autoscaler.enabled", "true");
        conf.setString("job.autoscaler.scaling.enabled", "false");
        conf.setString("job.autoscaler.stabilization.interval", "30 s");
        conf.setString("job.autoscaler.metrics.window", "1 m");
        conf.setString("job.autoscaler.scale-up.grace-period", "1 m");
        conf.setString("rest.port", "8081");

        // Enable the unaligned checkpoint to ensure the checkpoint can be finished.
        conf.setString("execution.checkpointing.unaligned", "true");
        conf.setString("execution.checkpointing.aligned-checkpoint-timeout", "1 s");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(10));
        env.setParallelism(1);
//        env.setMaxParallelism(1024);
        env.setMaxParallelism(840);

        env.fromSource(KafkaSource.<SamplePojo>builder()
                        .setBootstrapServers("localhost:9092")
                        .setTopics("quickstart-events")
                        .setGroupId("test")
                        .setStartingOffsets(OffsetsInitializer.latest())
                        .setValueOnlyDeserializer(new JsonDeserializationSchema<>(SamplePojo.class))
                        .build(),
                WatermarkStrategy.noWatermarks(),
                "My_Source")
                .map(v -> v)
                .rebalance()
                .map(new RateLimiterSleepMapFunction<>(1000))
                .name("RateLimiterMapFunction")
                .rebalance()
                .addSink(new DiscardingSink<>())
                .name("MySink");;

        env.execute();
    }
}
