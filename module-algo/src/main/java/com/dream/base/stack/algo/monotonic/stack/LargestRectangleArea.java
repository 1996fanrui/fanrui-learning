package com.dream.base.stack.algo.monotonic.stack;

import java.util.Stack;

/**
 * @author fanrui
 * @time 2020-04-04 21:49:33
 * 84. 柱状图中最大的矩形
 * LeetCode 84: https://leetcode-cn.com/problems/largest-rectangle-in-histogram/
 */
public class LargestRectangleArea {
    public int largestRectangleArea(int[] heights) {
        if(heights == null || heights.length == 0){
            return 0;
        }
        // 从栈底到栈顶单调递增的栈，存储 index
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        int res = 0;
        for(int i = 0; i < heights.length; i++){
            // 栈不为空，且 当前元素小于等于栈顶元素，则弹出栈顶元素
            while(!stack.isEmpty() && heights[i] <= heights[stack.peek()]){
                int cur = stack.pop();
                // 计算 当前高度对应的左边界，
                int left = stack.isEmpty() ? -1 : stack.peek();
                // 当前的 i 就是右边界，根据左右边界计算出 宽度
                int width = i - left - 1;
                res = Math.max(res, width * heights[cur]);
            }
            stack.push(i);
        }
        // 栈中不为空，表示右边没有比这些元素更小的值，即：栈中元素对应的右边界都是 heights.length
        while(!stack.isEmpty()){
            int cur = stack.pop();
            // 计算 当前高度对应的左边界，
            int left = stack.isEmpty() ? -1 : stack.peek();
            // 当前的 height.length 就是右边界，根据左右边界计算出 宽度
            int width = heights.length - left - 1;
            res = Math.max(res, width * heights[cur]);
        }
        return res;
    }
}
