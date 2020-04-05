package com.dream.window;

import java.util.LinkedList;

/**
 * @author fanrui
 * @time 2020-04-05 00:12:57
 * 面试题59 - II. 队列的最大值
 * 剑指 59-II： https://leetcode-cn.com/problems/dui-lie-de-zui-da-zhi-lcof/
 */
public class MaxQueue {

    // queue 存放所有原始数据
    LinkedList<Integer> queue;
    // 存放窗口的 最大值，是一个降序的 queue，队首是当前的 最大值
    LinkedList<Integer> maxQueue;
    public MaxQueue() {
        queue = new LinkedList<>();
        maxQueue = new LinkedList<>();
    }

    // 返回队首元素
    public int max_value() {
        if(queue.isEmpty()){
            return -1;
        }
        return maxQueue.peekFirst();
    }

    // 将 value 加入到 queue 的尾部，并更新 maxQueue
    public void push_back(int value) {
        queue.add(value);
        // 队列不为空，则将队列尾部比 value 小的元素全 移除
        while(!maxQueue.isEmpty() && maxQueue.peekLast() < value){
            maxQueue.pollLast();
        }
        // 当前元素入队
        maxQueue.add(value);
    }

    public int pop_front() {
        if(queue.isEmpty()){
            return -1;
        }
        int cur = queue.peekFirst();
        // 当前弹出的元素是 maxQueue 的队首元素，则需要将 maxQueue 队首元素弹出
        if(cur == maxQueue.peekFirst()){
            maxQueue.pollFirst();
        }
        return queue.pollFirst();
    }
}

/**
 * Your MaxQueue object will be instantiated and called as such:
 * MaxQueue obj = new MaxQueue();
 * int param_1 = obj.max_value();
 * obj.push_back(value);
 * int param_3 = obj.pop_front();
 */
