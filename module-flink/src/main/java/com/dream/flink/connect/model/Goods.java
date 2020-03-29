package com.dream.flink.connect.model;

import lombok.Data;

/**
 * @author fanrui
 * @time 2020-03-29 15:07:17
 * 商品的详细信息
 */
@Data
public class Goods {

    /** 商品id */
    int goodsId;

    /** 价格 */
    String goodsName;

    /**
     * 当前商品是否被下架，如果下架应该从 State 中去移除
     * true 表示下架
     * false 表示上架
     */
    boolean isRemove;
}
