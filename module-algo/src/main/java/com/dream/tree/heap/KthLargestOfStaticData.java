package com.dream.tree.heap;


import java.util.PriorityQueue;

/**
 * @author fanrui
 * 215. 数组中的第K个最大元素(静态数据)
 * LeetCode 215： https://leetcode-cn.com/problems/kth-largest-element-in-an-array/
 */
public class KthLargestOfStaticData {


    // 思路一：小顶堆实现，时间复杂度 O(n * lg n)
    public int findKthLargest(int[] nums, int k) {

        if (nums == null || nums.length == 0 || k == 0) {
            return -1;
        }
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int i = 0; i < nums.length; i++) {
            if (minHeap.size() < k) {
                minHeap.offer(nums[i]);
            } else if (minHeap.peek() < nums[i]) {
                minHeap.poll();
                minHeap.offer(nums[i]);
            }
        }
        return minHeap.peek();
    }

    // 思路二：利用快排思想，时间复杂度 O（n）
    private int[] nums;
    private int k;

    public int findKthLargest2(int[] nums, int k) {
        this.nums = nums;
        this.k = nums.length - k;
        return select(0, nums.length - 1);
    }

    private int select(int left, int right) {
        int pivot_index = partition(left, right, medium(left, right));
        if (k == pivot_index) {
            return nums[k];
        } else if (k < pivot_index) {
            return select(left, pivot_index - 1);
        } else {
            return select(pivot_index + 1, right);
        }
    }

    private int partition(int left, int right, int pivot_index) {
        int start = left, end = right;
        if (left != pivot_index) {
            swap(left, pivot_index);
        }
        int pivot = nums[left];
        while (start < end) {
            // 在右边，找比 pivot 小的数，将其换到左边
            while (start < end && pivot <= nums[end]) {
                end--;
            }
            nums[start] = nums[end];
            // 在左边，找比 pivot 大的数，将其换到右边
            while (start < end && nums[start] <= pivot) {
                start++;
            }
            nums[end] = nums[start];
        }
        // pivot 换到该去的位置
        nums[start] = pivot;
        return start;
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
