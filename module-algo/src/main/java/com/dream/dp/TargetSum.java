package com.dream.dp;

/**
 * @author fanrui
 * 目标和
 * LeetCode 494：https://leetcode-cn.com/problems/target-sum/
 */
public class TargetSum {


    public int findTargetSumWays(int[] nums, int S) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        // dp 数组最大值
        int[] dp = new int[2001];

        dp[nums[0] + 1000] = 1;
        dp[-nums[0] + 1000] += 1;
        for (int i = 1; i < nums.length; i++) {
            int[] next = new int[2001];
            for (int j = -1000; j <= 1000; j++) {
                int down = j - nums[i] + 1000;
                if (down >= 0) {
                    next[j + 1000] = dp[down];
                }

                int up = j + nums[i] + 1000;
                if (up < 2001) {
                    next[j + 1000] += dp[up];
                }
            }
            dp = next;
        }
        return S >= -1000 && S <= 1000 ? dp[S + 1000] : 0;
    }


}
