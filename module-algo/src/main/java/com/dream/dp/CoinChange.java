package com.dream.dp;

/**
 * @author fanrui
 * 322. 零钱兑换：https://leetcode-cn.com/problems/coin-change/
 * 给定不同面额的硬币 coins 和一个总金额 amount。
 * 编写一个函数来计算可以凑成总金额所需的最少的硬币个数。
 * 如果没有任何一种硬币组合能组成总金额，返回 -1。
 */
public class CoinChange {

    public int coinChange(int[] coins, int amount) {

        if (amount < 0 || coins == null || coins.length == 0) {
            return -1;
        }

        int[] dp = new int[amount + 1];

        for (int i = 1; i <= amount; i++) {
            // 步数不可能大于 amount，所以赋值为 amount + 1
            dp[i] = amount + 1;
            for (int j = 0; j < coins.length; j++) {
                if (i >= coins[j]) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

}
