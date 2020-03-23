package com.dream.math;

/**
 * @author fanrui
 * 下一个排列：实现获取下一个排列的函数，算法需要将给定数字序列重新排列成字典序中下一个更大的排列。
 * LeetCode 31：https://leetcode-cn.com/problems/next-permutation/
 */
public class NextPermutation {

    public void nextPermutation(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return;
        }

        for (int i = nums.length - 2; i >= 0; i--) {
            if (nums[i] < nums[i + 1]) {
                // 找 i 后面大于 该值的最小值 j。
                int j = i + 1;
                for (; j < nums.length; j++) {
                    if (nums[j] <= nums[i]) {
                        break;
                    }
                }
                // 找到第一个小于等于 i 的元素，那么 j--，就是我们要找的结果
                j--;

                swap(nums, i, j);
                reverse(nums, i + 1, nums.length - 1);
                return;
            }
        }
        reverse(nums, 0, nums.length - 1);
        return;
    }

    private void reverse(int[] nums, int i, int j) {
        while (i < j) {
            swap(nums, i, j);
            i++;
            j--;
        }
    }


    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }


}
