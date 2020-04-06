package com.dream.dp;

import java.util.LinkedList;

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
            // todo 注： 这里不能赋值为 Integer.MAX_VALUE，
            // 假如硬币面值只有 2，位置 1 不可达。3 位置也不可达，1 位置计算的需要金币数为 Integer.MAX_VALUE
            // 3位置可以从 1 位置来: dp[3] = dp[1]+1，结果越界为负数，直接错了。
            dp[i] = amount + 1;
            for (int j = 0; j < coins.length; j++) {
                if (i >= coins[j]) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }


    // bfs 来做，第一次遍历到 amount，当前的层数，就是最少的硬币个数
    public int coinChange1(int[] coins, int amount) {
        if (amount == 0) {
            return 0;
        }
        if (amount < 0 || coins == null || coins.length == 0) {
            return -1;
        }
        LinkedList<Entry> queue = new LinkedList<>();
        boolean[] visited = new boolean[amount];
        // 初始化将 0 放到 queue 中，且对应的 level 为 0
        queue.add(new Entry(0, 0));
        while (!queue.isEmpty()) {
            Entry curEntry = queue.pollFirst();
            // 下一层的层数
            int nextLevel = curEntry.level + 1;
            // 当前遍历到的位置
            int curValue = curEntry.value;
            for (int i = 0; i < coins.length; i++) {
                // 遍历从当前位置可以到达的所有位置
                int curPoint = curValue + coins[i];
                // 到达 amount 位置，表示找到了结果
                if (curPoint == amount) {
                    return nextLevel;
                }
                // 超过了 amount 不考虑，如果当前位置已经访问过，也不考虑，只考虑第一次遍历到该位置的情况
                if (curPoint > amount || visited[curPoint]) {
                    continue;
                }
                queue.add(new Entry(curPoint, nextLevel));
                visited[curPoint] = true;
            }
        }
        return -1;

    }

    private class Entry {
        int value;
        int level;

        Entry(int value, int level) {
            this.value = value;
            this.level = level;
        }
    }
}
