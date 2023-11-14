package com.dream.flink.kafka.demo;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.shaded.guava31.com.google.common.util.concurrent.RateLimiter;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaOldConsumer {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
//        conf.setString("execution.savepoint.path", "file:///tmp/flinkjob/3fcd6b559a1ec9475b310d9ee8e5c834/chk-4");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.setParallelism(2);
        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(10));

        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
        env.addSource(
                new FlinkKafkaConsumer<>("quickstart-events",
                        new JsonDeserializationSchema<>(SamplePojo.class),
                        consumerProps)
                        .setStartFromLatest())
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
