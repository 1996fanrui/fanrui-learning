package com.dream.dp;

/**
 * @author fanrui
 * @time 2020-03-21 22:31:16
 * 礼物的最大价值（二维矩阵，从左上角到右下角，求最长路径）
 * 剑指 Offer 47： https://leetcode-cn.com/problems/li-wu-de-zui-da-jie-zhi-lcof/
 */
public class MaxPathOfMatrix {

    public int maxValue(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int[] dp = new int[grid[0].length];

        // 初始化 dp 数组
        dp[0] = grid[0][0];
        for (int i = 1; i < grid[0].length; i++) {
            // 第一行，只能从左边达到该位置
            dp[i] = dp[i - 1] + grid[0][i];
        }

        for (int row = 1; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                // 第一列，只能从上一行到达，用上一行的路径 加 本位置的路径
                if (col == 0) {
                    dp[col] = dp[col] + grid[row][col];
                } else {
                    // 可以从左边或者上面到达，上一行路径 与 左边路径求最大值。加 本位置的路径
                    dp[col] = Math.max(dp[col], dp[col - 1]) + grid[row][col];
                }
            }
        }

        return dp[grid[0].length - 1];
    }

}
