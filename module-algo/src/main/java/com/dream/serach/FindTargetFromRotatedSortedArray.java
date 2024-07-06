package com.dream.serach;

/**
 * 旋转数组中 找 target 元素的 index，如果找不到返回 -1.
 * Leetcode 33: https://leetcode.cn/problems/search-in-rotated-sorted-array/
 */
public class FindTargetFromRotatedSortedArray {

    public static int search(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + ((right - left) >> 1);
            if (nums[mid] == target) {
                return mid;
            }
            // 左半个数组有序 (注：这里一定是 <=，因为左半个数组可能只剩下一个元素)
            if (nums[left] <= nums[mid]) {
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // 右半个数组有序
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] nums = new int[2];
        nums[0] = 3;
        nums[1] = 1;

        System.out.println(search(nums, 1));
    }

}
