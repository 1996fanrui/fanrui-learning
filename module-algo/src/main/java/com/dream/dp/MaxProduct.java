package com.dream.dp;

/**
 * @author fanrui
 * 乘积最大子序列
 * LeetCode 152: https://leetcode-cn.com/problems/maximum-product-subarray/
 * 给定一个整数数组 nums ，找出一个序列中乘积最大的连续子序列（该序列至少包含一个数）。
 */
public class MaxProduct {

    // 方法一：用两个 dp 数组来维护状态值
    public int maxProduct(int[] nums) {

        if(nums == null || nums.length == 0){
            return 0;
        }

        int[] min = new int[nums.length];
        int[] max = new int[nums.length];

        int res = min[0] = max[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int curMax = nums[i]* max[i-1];
            int curMin = nums[i]* min[i-1];

            min[i] = Math.min(nums[i], Math.min(curMin, curMax));
            max[i] = Math.max(nums[i], Math.max(curMin, curMax));

            res = Math.max(max[i], res);
        }

        return res;
    }

    /**
     * 方法一：用两个 dp 数组来维护状态值，
     * 但是实际计算过程中， 只需要拿到上一次的 min 和 max 即可，因此不需要使用数组
      */
    public int maxProduct2(int[] nums) {

        if(nums == null || nums.length == 0){
            return 0;
        }

        int lastMin;
        int lastMax;

        int res = lastMin = lastMax = nums[0];

        for (int i = 1; i < nums.length; i++) {
            int curMax = nums[i]* lastMax;
            int curMin = nums[i]* lastMin;

            lastMin = Math.min(nums[i], Math.min(curMin, curMax));
            lastMax = Math.max(nums[i], Math.max(curMin, curMax));

            res = Math.max(lastMax, res);
        }

        return res;
    }

}
