package com.dream.base.array;

/**
 * @author fanrui
 * @time 2020-03-18 00:32:25
 * 剑指 Offer 03. 数组中重复的数字
 */
public class FindRepeatNumber {

    public int findRepeatNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        for (int i = 0; i < nums.length;) {
            if (nums[i] != i) {
                // nums[i] 位置上存储的是 nums[i]，则找到了重复的数
                if(nums[nums[i]] == nums[i]){
                    return nums[i];
                }

                // swap i 位置和 nums[i] 位置的数
                int tmp = nums[i];
                nums[i] = nums[nums[i]];
                nums[tmp] = tmp;
            } else {
                i++;
            }
        }
        return -1;
    }

}
