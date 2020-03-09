package com.dream.dp.stock;

/**
 * @author fanrui
 * @time  2020-03-10 00:53:54
 * 买卖股票的最佳时机含手续费：
 * 无限次地完成交易，但是每次交易都需要付手续费。
 * LeetCode 714： https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/
 * 状态转移方程：
 * dp[i][0] = max(dp[i-1][0], dp[i-1][1] + prices[i])
 * dp[i][1] = max(dp[i-1][1], dp[i-1][0] - prices[i] - fee)
 */
public class BuySellStockWithFee {

    public int maxProfit(int[] prices, int fee) {

        if(prices == null || prices.length <= 1){
            return 0;
        }

        // 初始化，0位置上，未持有股票，所以 价格为 0
        int dp_i_0 = 0;
        // 初始化，0位置上，持有股票，
        // 所以 价格为 -prices[0] - fee (这里注意，需要扣手续费)
        int dp_i_1 = - prices[0] - fee;

        for (int i = 1; i < prices.length; i++) {
            int tmp = dp_i_0;
            dp_i_0 = Math.max(dp_i_0, dp_i_1 + prices[i]);
            dp_i_1 = Math.max(dp_i_1, tmp - prices[i] - fee);
        }

        return dp_i_0;
    }

}
