package com.dream.tree.heap;


import java.util.PriorityQueue;

/**
 * @author fanrui
 * @time 2020-03-13 12:52:27
 * 703. 数据流中的第K大元素
 * LeetCode 703：https://leetcode-cn.com/problems/kth-largest-element-in-a-stream/
 */
public class KthLargest {

    PriorityQueue<Integer> minHeap;
    int k;


    public KthLargest(int k, int[] nums) {
        minHeap = new PriorityQueue<>(k);
        this.k = k;

        for (int i = 0; i < nums.length; i++) {
            add(nums[i]);
        }
    }

    public int add(int val) {
        if (minHeap.size() < k) {
            minHeap.offer(val);
        } else if(minHeap.peek() < val){
            minHeap.poll();
            minHeap.offer(val);
        }
        return minHeap.peek();
    }

}
