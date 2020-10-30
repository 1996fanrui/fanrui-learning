package com.dream.flink.data;

import lombok.Data;

/**
 * @author fanrui
 * @time 2020-03-29 09:29:28
 * 订单的详细信息
 */
@Data
public class Order {

    public Order() {
    }

    public Order(long ts,
                 String orderId,
                 String userId,
                 int goodsId,
                 long price,
                 int cityId) {
        this.ts = ts;
        this.orderId = orderId;
        this.userId = userId;
        this.goodsId = goodsId;
        this.price = price;
        this.cityId = cityId;
    }

    /** 订单发生的时间 */
    public long ts;

    /** 订单 id */
    public String orderId;

    /** 用户id */
    public String userId;

    /** 商品id */
    public int goodsId;

    /** 价格 */
    public long price;

    /** 城市 */
    public int cityId;
}
