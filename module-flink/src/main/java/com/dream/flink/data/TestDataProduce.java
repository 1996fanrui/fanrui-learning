package com.dream.flink.data;

import com.dream.flink.data.proto.AppInfo;
import com.dream.flink.util.KafkaConfigUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.Scanner;



/**
 * @author fanrui
 * @date 2020-01-10 20:46:14
 */
public class TestDataProduce {

    private static final String TOPIC = "app-stat";
    private static Scanner sc = new Scanner(System.in);

    private static final Random random = new Random();

    private static void writeToKafka() {

        Properties props = new Properties();
        props.put("bootstrap.servers", KafkaConfigUtil.broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        // 随机生成 appId
        int appId = random.nextInt(2);
        System.out.println("请输入时间戳：");

        long timeMillis = sc.nextLong();

        AppInfo.Log data = AppInfo.Log
                .newBuilder()
                .setAppId(Integer.toString(appId))
                .setEventTime(timeMillis)
                .build();
        ProducerRecord record = new ProducerRecord<String, byte[]>(TOPIC,
                null, null, data.toByteArray());
        producer.send(record);
        System.out.println("发送数据: " + data);
        producer.flush();
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            writeToKafka();
        }
    }
}
