package com.dream.window;

import java.util.ArrayDeque;

/**
 * @author fanrui
 * 滑动窗口最大值
 * LeetCode 239：https://leetcode-cn.com/problems/sliding-window-maximum/
 * 给定一个数组 nums，有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。
 * 你只可以看到在滑动窗口内的 k 个数字。滑动窗口每次只向右移动一位。
 * 返回滑动窗口中的最大值。
 */
public class SlidingWindowMax {

    public int[] maxSlidingWindow(int[] nums, int k) {

        if (nums == null || nums.length == 0 || k == 0) {
            return new int[0];
        }

        int[] res = new int[nums.length - k + 1];
        // 单调递减的 queue
        ArrayDeque<Integer> queue = new ArrayDeque<>(k);

        for (int i = 0; i < nums.length; i++) {
            addDataToQueue(queue, nums[i]);
            // 初始化阶段
            if (i < k - 1) {
                continue;
            }
            res[i - k + 1] = queue.peekFirst();
            // 当前元素是队列中最大元素，直接出队
            if (nums[i - k + 1] == res[i - k + 1]) {
                queue.pollFirst();
            }
        }
        return res;
    }


    private static void addDataToQueue(ArrayDeque<Integer> queue, int data) {
        // queue 不为空，将队列中小于 当前数据的元素全部弹出
        while (!queue.isEmpty() && queue.peekLast() < data) {
            queue.pollLast();
        }
        queue.add(data);
    }

}
