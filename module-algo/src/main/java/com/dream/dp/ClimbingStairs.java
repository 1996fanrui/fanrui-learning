package com.dream.dp;

/**
 * @author fanrui
 * 爬楼梯
 * LeetCode 70:https://leetcode-cn.com/problems/climbing-stairs/
 * 剑指 Offer 10-II ：https://leetcode-cn.com/problems/qing-wa-tiao-tai-jie-wen-ti-lcof/submissions/
 * 需要 n 阶你才能到达楼顶。
 * 每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
 *
 */
public class ClimbingStairs {

    /**
     * 类似于斐波那契数列，可以将 dp 数组优化成 O（1）级别的空间复杂度，因为每次只需要 前两项即可
     * @param n
     * @return
     */
    public int climbStairs(int n) {

        if(n <= 2){
            return n;
        }

        int[] dp = new int[n+1];
        dp[1] = 1;
        dp[2] = 2;

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i-1] + dp[i-2];
        }

        return dp[n];
    }


    /**
     * 优化空间复杂度
     * @param n
     * @return
     */
    public int climbStairs2(int n) {

        if(n <= 2){
            return n;
        }


        int twoStepsBefore = 1;
        int oneStepBefore = 2;
        int allWays = 0;

        for (int i = 3; i <= n; i++) {
            // 当前的 路径为 前一个台阶的路径数 + 前两个台阶的路径数
            allWays = oneStepBefore + twoStepsBefore;
            // 前两个台阶的路径数要更新了
            twoStepsBefore = oneStepBefore;
            // 前一个台阶的路径数要更新了
            oneStepBefore = allWays;
        }

        return allWays;
    }
}
