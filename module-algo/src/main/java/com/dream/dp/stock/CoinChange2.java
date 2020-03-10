package com.dream.dp.stock;

/**
 * @author fanrui
 * @time 2020-03-10 21:30:04
 * 零钱兑换有多少种兑换方式
 * LeetCode 518：https://leetcode-cn.com/problems/coin-change-2/
 */
public class CoinChange2 {

    // 空间复杂度为 O（amount * coins.length）
    // 可以优化为 O（amount），详情往下翻即可
    public int change2(int amount, int[] coins) {

        if(amount <= 0 || coins == null || coins.length == 0){
            if(amount == 0){
                return 1;
            }
            return 0;
        }

        int[][] dp = new int[coins.length][amount+1];

        // 初始化 dp 方程，dp 数组的初始值为 coins[0] 的倍数位置上 全为 1，表示有一种方法到达该位置
        for (int i = 0; i*coins[0] <= amount; i++) {
            dp[0][i*coins[0]] = 1;
        }


        for (int i = 1; i < coins.length; i++) {
            for (int j = 0; j <= amount; j++) {
                if (j >= coins[i]) {
                    // 上一层位置，加上本层位置
                    dp[i][j] = dp[i-1][j] + dp[i][j-coins[i]];
                }
            }
        }

        return dp[coins.length-1][amount];
    }


    // 空间复杂度为 O（amount）
    public int change(int amount, int[] coins) {

        if(amount <= 0 || coins == null || coins.length == 0){
            if(amount == 0){
                return 1;
            }
            return 0;
        }

        int[] dp = new int[amount+1];

        // 初始化 dp 方程，dp 数组的初始值为 coins[0] 的倍数位置上 全为 1，表示有一种方法到达该位置
        for (int i = 0; i*coins[0] <= amount; i++) {
            dp[i*coins[0]] = 1;
        }


        for (int i = 1; i < coins.length; i++) {
            for (int j = 0; j <= amount; j++) {
                if (j >= coins[i]) {
                    // dp[j] = dp[j] + dp[j-coins[i]]; dp[j] 表示 前一层 相同的 价格有多少种可能
                    dp[j] += dp[j-coins[i]];
                }
            }
        }

        return dp[amount];
    }

}
