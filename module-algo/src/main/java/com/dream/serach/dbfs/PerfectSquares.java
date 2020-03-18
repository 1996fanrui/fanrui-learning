package com.dream.serach.dbfs;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author fanrui
 * @time 2020-03-18 12:33:49
 * 279 完全平方数
 * LeetCode 279： https://leetcode-cn.com/problems/perfect-squares/
 */
public class PerfectSquares {

    // 思路一： dp 思想，
    // dp[i] = min(dp[i],dp[i - j*j] +1) ， j 是从 1 遍历到根号 i，即：i - j*j 要 大于等于 0
    public int numSquaresDp(int n) {
        if (n <= 0) {
            return 0;
        }
        int[] dp = new int[n + 1];
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            // 最坏的情况就是每次+1
            dp[i] = i;
            // 只要 j * j <= i，那么 j*j 就是有效的路径
            for (int j = 1; j * j <= i; j++) {
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
            }
        }
        return dp[n];
    }


    // 思路二：bfs 思想
    // dp[i] = min(dp[i],dp[i - j*j] +1) ， j 是从 1 遍历到根号 i，即：i - j*j 要 大于等于 0
    public int numSquares(int n) {
        if (n <= 0) {
            return 0;
        }
        LinkedList<Entry> queue = new LinkedList<>();
        HashSet<Integer> set = new HashSet<>();

        // 元素 n 添加到 queue 中， 0 表示第 0 层元素
        queue.add(new Entry(n, 0));
        set.add(n);
        while (!queue.isEmpty()) {
            Entry curEntry = queue.pollFirst();
            int i = 1;
            for (; i * i < curEntry.data; i++) {
                // index 是当前 data 的下一层可以遍历到的元素
                int index = curEntry.data - i * i;
                //  已经遍历过，则 直接返回
                if (set.contains(index)) {
                    continue;
                }
                // 添加到 set 中，表示已经处理过
                set.add(index);
                // 当前元素 add 到 queue 中
                queue.add(new Entry(index, curEntry.level + 1));
            }
            //  到达了 0 位置，直接返回结果
            if (i * i == curEntry.data) {
                return curEntry.level + 1;
            }
        }
        return -1;
    }

    //  将当前元素与 level 封装
    class Entry {
        int data;
        int level;

        public Entry(int data, int level) {
            this.data = data;
            this.level = level;
        }
    }


}
