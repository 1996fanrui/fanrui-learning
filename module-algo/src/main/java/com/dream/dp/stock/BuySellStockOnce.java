package com.dream.dp.stock;

/**
 * @author fanrui
 * 买卖股票的最佳时机：
 *      只能持有一股，且只能买卖一次
 * LeetCode 121：https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock/
 */
public class BuySellStockOnce {

    public int maxProfit(int[] prices) {
        if(prices == null || prices.length <= 1){
            return 0;
        }

        // 结果：最大利润
        int maxProfit = 0;
        // 当前如果卖掉，最大的利润
        int curProfit;
        // 当前 i 之前的最小值
        int curMin = prices[0];

        for (int i = 1; i < prices.length; i++) {
            curMin = Math.min(curMin, prices[i]);
            curProfit = prices[i] - curMin;
            maxProfit = Math.max(maxProfit, curProfit);
        }
        return maxProfit;
    }
}
