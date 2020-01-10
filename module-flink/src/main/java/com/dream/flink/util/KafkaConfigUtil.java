package com.dream.flink.util;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author fanrui
 * @time 2020-01-04 15:41:10
 */
public class KafkaConfigUtil {

    private static final int RETRIES_CONFIG = 3;
    public static String broker_list = "test-kafka1.hadoop.bigdata.dmp.com:9092,test-kafka2.hadoop.bigdata.dmp.com:9092,test-kafka3.hadoop.bigdata.dmp.com:9092";

    /**
     *
     * @param groupId groupId
     * @return ConsumerProps
     */
    public static Properties buildConsumerProps( String groupId) {
        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker_list);
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "5000");
        consumerProps.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "20000");
        consumerProps.setProperty(FlinkKafkaConsumerBase.KEY_PARTITION_DISCOVERY_INTERVAL_MILLIS,
                                    Long.toString(TimeUnit.MINUTES.toMillis(1)));
        return consumerProps;
    }

    /**
     *
     * @return ProducerProps
     */
    public static Properties buildProducerProps() {
        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker_list);
        producerProps.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(RETRIES_CONFIG));
        producerProps.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "300000");
        producerProps.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        producerProps.setProperty(ProducerConfig.LINGER_MS_CONFIG, "3");
        producerProps.setProperty(ProducerConfig.ACKS_CONFIG, "1");

        return producerProps;
    }

}
