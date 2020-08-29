package com.dream.flink.kafka;

import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartitionAssigner;

public class ComputePartition {

    public static void main(String[] args) {
        String topic = "flink-merged_ad_log_report";
        int partition = 111;
        KafkaTopicPartition kafkaTopicPartition = new KafkaTopicPartition(topic, partition);

        int numParallelSubtasks = 400;
        int subtask = KafkaTopicPartitionAssigner.assign(kafkaTopicPartition, numParallelSubtasks);
        System.out.println(subtask);
    }

}
