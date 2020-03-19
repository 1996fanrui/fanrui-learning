package com.dream.tree.heap;


import java.util.Arrays;

/**
 * @author fanrui
 * @time  2020-03-20 01:03:57
 * 最小的k个数
 * 剑指 Offer 40：https://leetcode-cn.com/problems/zui-xiao-de-kge-shu-lcof/
 */
public class KthSmallestArrayOfStaticData {

    // 思路：利用快排思想，时间复杂度 O（n）
    private int[] nums;
    private int k;

    public int[] getLeastNumbers(int[] nums, int k) {
        if(k >= nums.length){
            return nums;
        }

        this.nums = nums;
        this.k = k;
        select(0, nums.length - 1);
        return Arrays.copyOf(nums, k);
    }

    private void select(int left, int right) {
        int[] pivot_index = partition(left, right, medium(left, right));
        if (k < pivot_index[0]) {
            select(left, pivot_index[0] - 1);
        } else if(k > pivot_index[1]){
            select(pivot_index[0] + 1, right);
        }
    }

    // 荷兰国旗问题，搞出三部分数据
    private int[] partition(int left, int right, int pivot_index) {
        if (left != pivot_index) {
            swap(left, pivot_index);
        }
        int pivot = nums[left];
        int less = left - 1;
        int l = left + 1;
        int more = right + 1;
        while (l < more) {
            if (nums[l] < pivot) {
                swap(++less, l++);
            } else if (nums[l] > pivot) {
                swap(--more, l);
            } else {
                l++;
            }
        }
        return new int[]{less + 1, l - 1};
    }

    // 数组中取三个数，便于 取三个数的中位数用于 pivot
    private int medium(int left, int right) {
        int medium = (left + right) / 2;
        if (nums[left] <= nums[medium] && nums[medium] <= nums[right]) {
            return medium;
        } else if (nums[medium] <= nums[left] && nums[left] <= nums[right]) {
            return left;
        } else {
            return right;
        }
    }

    private void swap(int index1, int index2) {
        int tmp = nums[index1];
        nums[index1] = nums[index2];
        nums[index2] = tmp;
    }
}
