package com.dream.serach;

/**
 * @author fanrui
 * @time 2020-03-22 01:20:45
 * 0～n-1中缺失的数字
 * 长度为n-1的递增排序数组中的所有数字都是唯一的，并且每个数字都在范围0～n-1之内。
 * 在范围0～n-1内的n个数字中有且只有一个数字不在该数组中，请找出这个数字。
 * 剑指 Offer 53-II：https://leetcode-cn.com/problems/que-shi-de-shu-zi-lcof/
 */
public class MissingNumber {

    public int missingNumber(int[] nums) {

        if (nums == null) {
            return -1;
        }

        int low = 0;
        int high = nums.length - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            // 如果 mid 位置放置的 mid，则往右找
            if (nums[mid] == mid) {
                low = mid + 1;
                // 这块一定要考虑是否到达了右边界，可能缺失的是最后一个元素 n
                if (mid == nums.length - 1) {
                    return low;
                }
            } else {
                // 如果 mid 位置放置的 mid+1

                // mid -1 位置放置的 mid，则往左找
                if (mid == 0 || nums[mid - 1] == mid - 1) {
                    return mid;
                }

                // mid -1 位置放置的 mid，则往左找
                high = mid - 1;
            }
        }
        return -1;
    }

}
