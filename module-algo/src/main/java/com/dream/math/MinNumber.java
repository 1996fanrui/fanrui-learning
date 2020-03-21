package com.dream.math;

import java.util.Arrays;

/**
 * @author fanrui
 * @time 2020-03-21 18:31:08
 * 数组排成最小的数
 * 剑指 Offer 45：https://leetcode-cn.com/problems/ba-shu-zu-pai-cheng-zui-xiao-de-shu-lcof/
 */
public class MinNumber {

    public String minNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            return "";
        }

        String[] strs = new String[nums.length];
        // 全部转移到 string 数组中
        for (int i = 0; i < nums.length; i++) {
            strs[i] = Integer.toString(nums[i]);
        }
        Arrays.sort(strs, (a, b) -> (a + b).compareTo(b + a));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]);
        }
        return sb.toString();
    }
}
