package com.dream.bit;

/**
 * @author fanrui
 * @time 2020-03-18 20:58:49
 * 136. 找出只出现一次的数字（其他数字出现了两次）
 * LeetCode 136： https://leetcode-cn.com/problems/single-number/
 */
public class SingleNumber {

    // 思路：数组中所有数进行异或操作，自己跟自己异或为 0，所以异或的最后结果就是结果
    public int singleNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int res = 0;
        for (int i = 0; i < nums.length; i++) {
            res ^= nums[i];
        }
        return res;
    }

}
