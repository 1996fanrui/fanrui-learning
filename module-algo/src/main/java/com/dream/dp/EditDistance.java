package com.dream.dp;

/**
 * @author fanrui
 * @time 2020-03-11 01:23:50
 * 编辑距离：给定两个单词 word1 和 word2，计算出将 word1 转换成 word2 所使用的最少操作数 。
 * LeetCode 72：https://leetcode-cn.com/problems/edit-distance/
 */
public class EditDistance {

    public int minDistance(String word1, String word2) {

        // word1 为空的情况
        if (word1 == null || "".equals(word1)) {
            if (word2 == null || "".equals(word2)) {
                return 0;
            }
            return word2.length();
        }

        char[] array1 = word1.toCharArray();

        // word2 为空的情况
        if (word2 == null || "".equals(word2)) {
            return array1.length;
        }

        char[] array2 = word2.toCharArray();


        // 初始化 dp 方程
        int[][] dp = new int[array1.length + 1][array2.length + 1];
        for (int i = 0; i <= array1.length; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= array2.length; j++) {
            dp[0][j] = j;
        }

        // dp 递推
        for (int i = 1; i <= array1.length; i++) {
            for (int j = 1; j <= array2.length; j++) {
                if (array1[i - 1] == array2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j],
                            Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                }
            }
        }

        return dp[array1.length][array2.length];
    }

}
