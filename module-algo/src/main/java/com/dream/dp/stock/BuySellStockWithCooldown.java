package com.dream.dp.stock;

/**
 * @author fanrui
 * @time  2020-03-10 00:31:35
 * 最佳买卖股票时机含冷冻期：
 * 不限制股票购买的总次数，但是限制：卖出股票后，无法在第二天买入股票 (即冷冻期为 1 天)。
 * LeetCode 309: https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/
 * 状态转移方程：
 * dp[i][0] = max(dp[i-1][0], dp[i-1][1] + prices[i])
 * dp[i][1] = max(dp[i-1][1], dp[i-2][0] - prices[i])
 */
public class BuySellStockWithCooldown {

    public int maxProfit(int[] prices) {

        if(prices == null || prices.length <= 1){
            return 0;
        }

        // 初始化，0位置上，未持有股票，所以 价格为 0
        int dp_i_0 = 0;
        // 初始化，0位置上，持有股票，所以 价格为 -prices[0]
        int dp_i_1 = -prices[0];
        // 表示 dp[i-2][0] ， i 从 1 开始，
        // dmp_pre_0 初始值表示 dp[-1][0]，默认认为 0 即可
        int dmp_pre_0 = 0;

        for (int i = 1; i < prices.length; i++) {
            int tmp = dp_i_0;
            dp_i_0 = Math.max(dp_i_0, dp_i_1 + prices[i]);
            dp_i_1 = Math.max(dp_i_1, dmp_pre_0 - prices[i]);
            dmp_pre_0 = tmp;
        }

        return dp_i_0;
    }

}
