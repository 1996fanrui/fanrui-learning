package com.dream.flink.data;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Random;

public class OrderGenerator extends RichParallelSourceFunction<Order> {

    private static final Random random = new Random();
    private static long orderId = 0;

    private int userIdMax = 10_000_000;

    @Override
    public void run(SourceContext sourceContext) throws Exception {
        while (true) {

            int cityId = random.nextInt(10);
            if (cityId == 0) {
                sourceContext.collect(null);
            }

            String orderId = "orderId:" + OrderGenerator.orderId++;
            String userId = Integer.toString(random.nextInt(userIdMax));
            int goodsId = random.nextInt(10);
            int price = random.nextInt(10000);
            Order order = new Order(System.currentTimeMillis(),
                    orderId, userId, goodsId, price, cityId);
            sourceContext.collect(order);
        }
    }

    @Override
    public void cancel() {

    }
}
