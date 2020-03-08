package com.dream.dp;

import java.util.List;

/**
 * @author fanrui
 * @time 2020-03-09 01:15:10
 * 三角形最小路径和：
 * 给定一个三角形，找出自顶向下的最小路径和。每一步只能移动到下一行中相邻的结点上。
 * LeetCode 120：https://leetcode-cn.com/problems/triangle/
 * 思路，从最下层往上层递推即可
 */
public class Triangle {

    public int minimumTotal(List<List<Integer>> triangle) {

        if(triangle == null || triangle.size() == 0){
            return 0;
        }

        int floor = triangle.size();
        int[] dp = new int[floor];

        // 初始化 dp 数组
        List<Integer> lastFloor = triangle.get(floor - 1);
        for (int i = 0; i < floor; i++) {
            dp[i] = lastFloor.get(i);
        }

        // 从 倒数第二层开始，一直到 第一层
        for (int i = floor-1; i > 0; i--) {
            List<Integer> curFloor = triangle.get(i-1);
            for (int j = 0; j < i; j++) {
                // 当前的路径长为 当前点的 data + 两种路径的最小值
                dp[j] = curFloor.get(j) + Math.min(dp[j],dp[j+1]);
            }
        }

        return dp[0];
    }

}
