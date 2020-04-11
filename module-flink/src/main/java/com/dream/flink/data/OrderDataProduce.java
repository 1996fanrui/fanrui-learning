package com.dream.flink.data;

import com.alibaba.fastjson.JSON;
import com.dream.flink.util.KafkaConfigUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * @author fanrui
 * @date 2020-04-10 21:53:39
 */
public class OrderDataProduce {

    private static final String TOPIC = "order";

    private static final Random random = new Random();

    private static long orderId = random.nextInt(10000);

    private static void writeToKafka() throws InterruptedException {

        Properties props = new Properties();
        props.put("bootstrap.servers", KafkaConfigUtil.broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);

        for (int i = 0; i < 10; i++) {
            // 随机生成订单数据
            Order order = Order.builder()
                    .time(System.currentTimeMillis())
                    .orderId(Long.toString(orderId++))
                    .userId(Integer.toString(random.nextInt(100)))
                    .goodsId(random.nextInt(50))
                    .price(random.nextInt(10000))
                    .cityId(random.nextInt(20))
                    .build();

            String data = JSON.toJSONString(order);
            ProducerRecord record = new ProducerRecord<String, String>(TOPIC,
                    null, null, data);
            producer.send(record);
            System.out.println("发送数据: " + data);
            TimeUnit.MILLISECONDS.sleep(100);
        }
        producer.flush();
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            writeToKafka();
        }
    }
}
