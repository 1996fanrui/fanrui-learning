package com.dream.dp.stock;

/**
 * @author fanrui
 * 买卖股票的最佳时机：
 *      只能持有一股，且只能买卖 K 次
 * LeetCode 188： https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-iv/
 */
public class BuySellStockK {

    public static int maxProfit(int k, int[] prices) {
        if(prices == null || prices.length <= 1){
            return 0;
        }

        // 即：不限制买卖次数(只要交易次数在 n/2 次以上，都可以认为不限制买卖次数)
        if(k > prices.length/2){
            int res = 0;
            for (int i = 0; i < prices.length-1; i++) {
                if(prices[i] < prices[i+1]){
                    res += prices[i+1] - prices[i];
                }
            }
            return res;
        }

        // 二维数组，第一维表示 0-k 次交易，这里是买入时 k +1
        // 第二维表示 是否持有，0表示未持有，1表示持有
        int[][] dp = new int[k+1][2];

        dp[0][0] = 0;
        // 第一维 k = 0，表示还没进行任何交易，此时不可能持有股票，因此 dp[0][1] 赋值为 Integer.MIN_VALUE
        dp[0][1] = Integer.MIN_VALUE;
        for (int i = 0; i < prices.length; i++) {
            for (int kk = k; kk >= 1; kk--) {
                if(i==0){
                    dp[kk][0] = 0;
                    // i == 0 表示第0个位置持有股票，kk次交易，当前余额是 -prices[0]
                    dp[kk][1] = -prices[0];
                    continue;
                }
                // i 位置没有操作，或者 i-1 位置持有，且 i 位置卖掉（卖掉，所以 + prices[i]）
                dp[kk][0] = Math.max(dp[kk][0], dp[kk][1] + prices[i]);
                // i 位置没有操作，或者 i-1 位置未持有，且 i 位置买入（买入，所以 - prices[i]）
                // 注意：买入时 k 的数量 + 1
                dp[kk][1] = Math.max(dp[kk][1], dp[kk-1][0] - prices[i]);
            }
        }

        // 结果：最大利润
        int maxProfit = 0;

        // 检查所有通过 0 次 ~ k 次买卖股票后，到达终点，看谁的 利润最高
        for (int kk = 0; kk <= k; kk++) {
            maxProfit = Math.max(maxProfit, dp[kk][0]);
        }

        return maxProfit;
    }

    public static void main(String[] args) {
        System.out.println(maxProfit(2, new int[]{4,3,2,1}));
    }
}
