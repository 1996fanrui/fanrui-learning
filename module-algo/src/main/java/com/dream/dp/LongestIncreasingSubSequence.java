package com.dream.dp;

/**
 * @author fanrui
 * @time 2020-03-10 13:09:35
 * 300. 最长上升子序列，subsequence 表示非连续
 * LeetCode 300: https://leetcode-cn.com/problems/longest-increasing-subsequence/
 */
public class LongestIncreasingSubSequence {

    // dp 的思路
    public int lengthOfLIS_DP(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int[] dp = new int[nums.length];
        int res = 1;

        for (int i = 0; i < nums.length; i++) {
            dp[i] = 1;
            // 检查 i 之前的所有元素
            for (int j = 0; j < i; j++) {
                // 当前元素大于之前的元素，则 更新 当前的 dp 值
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            res = Math.max(res, dp[i]);
        }

        return res;
    }


    // 维护一个最长子序列数组的思想，时间复杂度 O（n lgn）
    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int[] LIS = new int[nums.length];
        int length = 1;
        LIS[0] = nums[0];

        for (int i = 0; i < nums.length; i++) {
            // 当前元素大于 最长子序列最大值
            if (nums[i] > LIS[length - 1]) {
                // 当前元素添加到最长子序列，且子序列长度 +1
                LIS[length++] = nums[i];
            } else {
                int minIndex = getMinIndexOfGeData(LIS, 0, length-1, nums[i]);
                LIS[minIndex] = nums[i];
            }
        }

        return length;
    }


    // 查找 大于等于 data 的最小数的 index
    private static int getMinIndexOfGeData(int[] arr, int left, int right, int data){

        while (left < right){
            int mid = left + ((right - left) >> 1);
            if(data > arr[mid]){
                left = mid + 1;
            } else if(data < arr[mid]){
                right = mid - 1;
            } else {
                return mid;
            }
        }
        if(arr[left] < data){
            return left+1;
        }
        return left;
    }

    public static void main(String[] args) {
        new LongestIncreasingSubSequence().lengthOfLIS(new int[]{1,2,3,3,3,100,4,5,6});
    }
}
