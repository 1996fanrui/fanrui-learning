package com.dream.sum;

/**
 * @author fanrui
 * 两数之和（数组元素升序）
 * 剑指 57： https://leetcode-cn.com/problems/he-wei-sde-liang-ge-shu-zi-lcof/
 */
public class TwoSumAscending {

    public int[] twoSum(int[] nums, int target) {
        int[] res = new int[2];
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            if (nums[left] + nums[right] == target) {
                res[0] = nums[left];
                res[1] = nums[right];
                break;
            } else if (nums[left] + nums[right] > target) {
                // 和大于 target，right 指针左移
                right--;
            } else {
                // 和小于 target，left 指针右移。
                left++;
            }
        }
        return res;
    }
}
