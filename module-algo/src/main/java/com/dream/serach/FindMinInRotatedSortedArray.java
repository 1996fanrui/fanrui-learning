package com.dream.serach;

/**
 * @author fanrui
 * @time  2020-03-18 23:39:11
 * 旋转数据的最小数字（数据无重复）
 * LeetCode 153：https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array/
 */
public class FindMinInRotatedSortedArray {

    public int findMin(int[] nums) {
        if(nums == null || nums.length == 0){
            return -1;
        }

        int start = 0;
        int end = nums.length - 1;
        int res = 0;
        while(nums[start] > nums[end]){
            if(start + 1 == end){
                res = end;
                break;
            }
            int mid = start + ((end-start)>>1);
            if(nums[mid] > nums[start]){
                start = mid;
            } else {
                end = mid;
            }
        }
        return nums[res];
    }



}
