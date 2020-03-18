package com.dream.serach;

/**
 * @author fanrui
 * @time 2020-03-19 00:16:20
 * 旋转数据的最小数字（数据有重复）
 * LeetCode 154：https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array-ii/
 * 剑指 Offer 11：https://leetcode-cn.com/problems/xuan-zhuan-shu-zu-de-zui-xiao-shu-zi-lcof/
 */
public class FindMinInRotatedSortedArrayIncludeRepeatData {

    public int findMin(int[] nums) {
        if(nums == null || nums.length == 0){
            return -1;
        }
        int start = 0;
        int end = nums.length - 1;
        while(nums[start] >= nums[end]){
            if(start + 1 == end){
                return nums[end];
            }
            int mid = start + ((end-start)>>1);
            // start end mid 三个元素相等，只能遍历去查找
            if(nums[start] == nums[end] && nums[start] == nums[mid]){
                return findMinInOrder(nums, start, end);
            }

            // mid 元素大于等于 start，说明 start 和 mid 对应到同一个数组，所以 start 右移
            // 否则 end 左移
            if(nums[start] <= nums[mid]){
                start = mid;
            } else {
                end = mid;
            }
        }
        return nums[0];
    }

    // 遍历去查找，发现第一个小的元素就是要找的元素
    private int findMinInOrder(int[] nums, int left, int right){
        int result = nums[left];
        for(int i = left+1; i <= right; i++){
            if(nums[i] < result ){
                result = nums[i];
                break;
            }
        }
        return result;
    }


}
