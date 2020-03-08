package com.dream.sum.subarray;

/**
 * @author fanrui
 * 53. 最大子序和
 * Leetcode 53:https://leetcode-cn.com/problems/maximum-subarray/
 * 贪心：如果当前值为 正数，且前面的累加和为 负数，那么还不如直接舍弃 前面的数
 * 也就是 用 当前的数，和 sum+当前数，求个 max 即可
 */
public class MaxSubArray {
    public int maxSubArray(int[] nums) {
        int sum = nums[0];
        int res = nums[0];
        for (int i = 1; i < nums.length; i++) {
            sum = Math.max(sum + nums[i], nums[i]);
            res = Math.max(sum, res);
        }
        return res;
    }
}
