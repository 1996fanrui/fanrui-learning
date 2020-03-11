package com.dream.base.array;

/**
 * @author fanrui
 * 移动零:将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 * LeetCode 283：https://leetcode-cn.com/problems/move-zeroes/
 */
public class MoveZeroes {

    public void moveZeroes(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return;
        }
        int slow = 0;
        for (int i = 0; i < nums.length; i++) {
            // 将 i 与 slow 位置交换， slow ++
            if (nums[i] != 0) {
                int tmp = nums[slow];
                nums[slow++] = nums[i];
                nums[i] = tmp;
            }
        }
    }

}
