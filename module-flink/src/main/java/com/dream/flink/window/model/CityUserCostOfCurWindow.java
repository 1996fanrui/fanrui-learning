package com.dream.flink.window.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fanrui
 * @time 2020-03-29 21:02:53
 * 当前窗口指定城市指定用户消费的金额
 */
@Data
@AllArgsConstructor
public class CityUserCostOfCurWindow {
    /** 窗口的结束时间 */
    long time;

    /** 城市 */
    int cityId;

    /** 用户id */
    String userId;

    /** 消费的总金额 */
    long cost;
}
