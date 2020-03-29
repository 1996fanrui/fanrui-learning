package com.dream.flink.connect.model;

import lombok.Data;

/**
 * @author fanrui
 * @time 2020-03-29 09:29:28
 * 订单的详细信息
 */
@Data
public class Order {
    /** 订单发生的时间 */
    long time;

    /** 订单 id */
    String orderId;

    /** 用户id */
    String userId;

    /** 商品id */
    int goodsId;

    /** 价格 */
    int price;

    /** 城市 */
    int cityId;
}
