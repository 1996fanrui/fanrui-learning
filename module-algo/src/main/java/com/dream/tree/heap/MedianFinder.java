package com.dream.tree.heap;

import java.util.PriorityQueue;

/**
 * @author fanrui
 * @time 2020-03-21 14:56:53
 * 295. 数据流的中位数
 * LeetCode 295： https://leetcode-cn.com/problems/find-median-from-data-stream/
 * 剑指 Offer 41： https://leetcode-cn.com/problems/shu-ju-liu-zhong-de-zhong-wei-shu-lcof/
 */
public class MedianFinder {

    // maxHeap 大顶堆存放较小的一半数据
    PriorityQueue<Integer> maxHeap;
    // minHeap 小顶堆存放较大的一半数据
    PriorityQueue<Integer> minHeap;

    /**
     * initialize your data structure here.
     */
    public MedianFinder() {
        // 大顶堆初始化，直接使用 Collections.reverseOrder() 即可
        maxHeap = new PriorityQueue<>((a, b) -> b - a);
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        // 初始化情况下，大顶堆为空，先插入大顶堆
        if (maxHeap.isEmpty()) {
            maxHeap.offer(num);
            return;
        }

        // 插入的数据小于等于 大顶堆的堆顶，直接插入到 大顶堆
        if (num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }
        rebalance();
    }

    // 保证大顶堆元素永远大于等于小顶堆。且元素个数差值不能大于1.
    private void rebalance() {
        int diff = maxHeap.size() - minHeap.size();

        // 大顶堆中元素个数 超过 小顶堆元素个数 1，则大顶堆需要搬运至小顶堆
        if (diff > 1) {
            minHeap.offer(maxHeap.poll());
        } else if (diff < 0) {
            // 大顶堆中元素个数 少于 小顶堆元素个数
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() == 0) {
            return 0;
        }
        // 容量相等，取两者的平均值
        if (maxHeap.size() == minHeap.size()) {
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }

        // 容量不等，从 大顶堆中去取
        return maxHeap.peek();
    }


    public static void main(String[] args) {
        MedianFinder medianFinder = new MedianFinder();
        medianFinder.addNum(2);
        System.out.println(medianFinder.findMedian());

        medianFinder.addNum(3);
        System.out.println(medianFinder.findMedian());
    }
}
