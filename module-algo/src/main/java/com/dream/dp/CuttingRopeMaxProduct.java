package com.dream.dp;

/**
 * @author fanrui
 * @time 2020-03-19 21:02:44
 * 剪绳子（整数拆分，求最大乘积）
 * LeetCode 343：https://leetcode-cn.com/problems/integer-break/
 * 剑指 Offer 14：https://leetcode-cn.com/problems/jian-sheng-zi-lcof/
 */
public class CuttingRopeMaxProduct {

    // dp 思路
    public int cuttingRopeDp(int n) {
        // 小于等于 3 的时候，给定结果
        if (n < 2) {
            return 0;
        } else if (n == 2) {
            return 1;
        } else if (n == 3) {
            return 2;
        }

        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        dp[3] = 3;
        // 递推计算 4~n 对应的结果
        for (int i = 4; i <= n; i++) {
            int max = 0;
            // 计算 i 的结果，j 需要遍历 1 到 i / 2，看哪个结果大
            for (int j = 1; j <= i / 2; j++) {
                max = Math.max(max, dp[j] * dp[i - j]);
            }
            dp[i] = max;
        }
        return dp[n];
    }


    // 贪心思路
    public int cuttingRope(int n) {
        // 小于等于 3 的时候，给定结果
        if (n < 2) {
            return 0;
        } else if (n == 2) {
            return 1;
        } else if (n == 3) {
            return 2;
        }

        // 计算 n 能拆分成几个 3
        int count3 = n / 3;
        if (n - count3 * 3 == 1) {
            count3--;
        }
        // 计算 2 的个数
        int count2 = (n - count3 * 3) / 2;
        return (int) Math.pow(3, count3) * (int) Math.pow(2, count2);
    }
}
