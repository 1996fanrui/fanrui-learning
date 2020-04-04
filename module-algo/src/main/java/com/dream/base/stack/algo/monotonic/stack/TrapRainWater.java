package com.dream.base.stack.algo.monotonic.stack;

import java.util.Stack;

/**
 * @author fanrui
 * @time 2020-04-04 22:24:14
 * 42. 接雨水
 * LeetCode 42： https://leetcode-cn.com/problems/trapping-rain-water/
 */
public class TrapRainWater {

    public int trap(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }
        // 借助从栈底到栈顶单调递减的单调栈。可以求出每个位置左右两边离自己最近的比自己大的数。
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        int res = 0;
        for (int i = 0; i < height.length; i++) {
            // 当前高度 大于等于 栈顶高度，则弹出栈顶高度，进行结算
            while (!stack.isEmpty() && height[i] >= height[stack.peek()]) {
                int curIndex = stack.pop();
                // i 位置高度 与 当前弹出 index 对应的高度 相同，则不结算面积
                if (height[i] == height[curIndex]) {
                    break;
                }
                // 栈中空了，表示左边没有更高的柱子，因此没法接雨水
                if (stack.isEmpty()) {
                    break;
                }

                int left = stack.peek();
                int width = i - left - 1;
                int curHeight = Math.min(height[left], height[i]) - height[curIndex];
                res += width * curHeight;
            }
            stack.push(i);
        }
        return res;
    }

}
