package com.dream.flink.data;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OrderGenerator extends RichParallelSourceFunction<Order> {

    private static final Random random = new Random();
    private static long orderId = 0;

    @Override
    public void run(SourceContext sourceContext) throws Exception {
        while (true) {
            TimeUnit.MILLISECONDS.sleep(100);

            int cityId = random.nextInt(10);
            if(cityId == 0){
                sourceContext.collect(null);
            }

            Order order = Order.builder()
                    .time(System.currentTimeMillis())
                    .orderId("orderId:" + orderId++)
                    .cityId(cityId)
                    .goodsId(random.nextInt(10))
                    .price(random.nextInt(10000))
                    .userId("UserId:" + random.nextInt(10000))
                    .build();
            sourceContext.collect(order);
        }
    }

    @Override
    public void cancel() {

    }
}
