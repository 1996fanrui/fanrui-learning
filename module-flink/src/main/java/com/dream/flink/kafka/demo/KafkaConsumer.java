package com.dream.flink.kafka.demo;

import com.dream.flink.kafka.alignment.SamplePojo;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.shaded.guava30.com.google.common.util.concurrent.RateLimiter;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KafkaConsumer {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.fromSource(KafkaSource.<SamplePojo>builder()
                        .setBootstrapServers("localhost:9092")
                        .setTopics("fast-topic")
                        .setGroupId("test")
                        .setStartingOffsets(OffsetsInitializer.latest())
                        .setValueOnlyDeserializer(new JsonDeserializationSchema<>(SamplePojo.class))
                        .build(),
                WatermarkStrategy.noWatermarks(),
                "FAST_SOURCE_NAME")
                .rebalance()
                .map(new LimitedMap<>(10))
                .rebalance()
                .print();

        env.execute();
    }



    static class LimitedMap<T> extends RichMapFunction<T, T> {
        private final double permitsPerSecond;
        private RateLimiter rateLimiter;

        LimitedMap(double permitsPerSecond) {
            this.permitsPerSecond = permitsPerSecond;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            this.rateLimiter = RateLimiter.create(permitsPerSecond);
        }

        @Override
        public T map(T record) throws Exception {
            rateLimiter.acquire();
            return record;
        }
    }
}
