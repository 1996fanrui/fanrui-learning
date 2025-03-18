package com.dream.flink.scheduler.autoscaler;

import com.dream.flink.func.map.RateLimiterSleepMapFunction;
import com.dream.flink.kafka.demo.SamplePojo;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

/**
 * Test for network memory is insufficient after scaling up, and it consume data from kafka.
 */
public class NetworkMemoryInsufficientConsumeKafka {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set(TaskManagerOptions.MINI_CLUSTER_NUM_TASK_MANAGERS, 3);
        conf.set(TaskManagerOptions.NUM_TASK_SLOTS, 20);
        conf.set(TaskManagerOptions.NETWORK_MEMORY_MIN, MemorySize.ofMebiBytes(10));
        conf.set(TaskManagerOptions.NETWORK_MEMORY_MAX, MemorySize.ofMebiBytes(10));

        conf.setString("rest.flamegraph.enabled", "true");
        conf.setString("execution.checkpointing.unaligned.enabled", "true");

        conf.setString("jobmanager.scheduler", "adaptive");
        conf.setString("job.autoscaler.enabled", "true");
        conf.setString("job.autoscaler.scaling.enabled", "true");
        conf.setString("job.autoscaler.stabilization.interval", "10s");
        conf.setString("job.autoscaler.scale-down.interval", "1m");
        conf.setString("job.autoscaler.metrics.window", "90s");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(5);

        env.enableCheckpointing(5000L);

        // Source rate is 10_000 records per second
        env.fromSource(KafkaSource.<SamplePojo>builder()
                        .setBootstrapServers("localhost:9092")
                        .setTopics("quickstart-events")
                        .setGroupId("test")
                        .setStartingOffsets(OffsetsInitializer.latest())
                        .setValueOnlyDeserializer(new JsonDeserializationSchema<>(SamplePojo.class))
                        .build(),
                WatermarkStrategy.noWatermarks(),
                "My_Source")
                .rebalance()
                .map(new RateLimiterSleepMapFunction<>(300))
                .name("RateLimiterMapFunction1")
                .rebalance()
                .map(new RateLimiterSleepMapFunction<>(1_000))
                .name("RateLimiterMapFunction2")
                .rebalance()
                .map(x -> x)
                .name("Nothing")
                .rebalance()
                .addSink(new DiscardingSink<>())
                .name("MySink");

        env.execute(NetworkMemoryInsufficientConsumeKafka.class.getSimpleName());
    }

}
