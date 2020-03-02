package com.dream.base.stack.algo.monotonic.stack;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

/**
 * @author fanrui
 * @time  2020-03-03 01:22:08
 *
 * 求最大子矩阵
 * 牛客题目链接：https://www.nowcoder.com/practice/ed610b2fea854791b7827e3111431056
 */
public class MaxSubMatrix {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String[] numbers = bufferedReader.readLine().split(" ");
        int n = Integer.valueOf(numbers[0]);
        int m = Integer.valueOf(numbers[1]);
        int[][] map = new int[n][m];
        for (int i = 0; i < n; i++) {
            numbers = bufferedReader.readLine().split(" ");
            for (int j = 0; j < m; j++) {
                map[i][j] = Integer.parseInt(numbers[j]);
            }
        }
        System.out.println(maxRecSize(map));
    }

    private static int maxRecSize(int[][] map) {
        if(map == null || map.length == 0 || map[0].length == 0){
            return 0;
        }

        int result = 0;
        int row = map.length;
        int column = map[0].length;
        int[] height = new int[column];

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < column; c++) {
                // 本层的高度可以依赖上一层高度来做
                height[c] = map[r][c] == 0 ? 0 : height[c] + 1;
            }
            // 每一层面积求最大值即可
            result = Math.max(result, maxRecFromBottom(height));
        }
        return result;
    }

    /**
     * 相当于寻找height中每个元素左边和右边的第一个比它小的元素，时间复杂度O(M)
     */
    private static int maxRecFromBottom(int[] height) {

        int result = 0;
        // 单调栈，栈底到栈顶，由小到大
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < height.length; i++) {
            // 把栈中大于 i 的数弹出
            while (!stack.isEmpty() && height[i] <= height[stack.peek()] ){
                int cur = stack.pop();
                // 弹出的元素高度为 0 ，不用计算面积，直接退出本次循环
                if(height[cur] == 0){
                    continue;
                }
                // 弹出的元素高度非 0 ，则用 高度 * 宽度，计算面积
                int left = stack.isEmpty() ? -1 :stack.peek();
                int width = i - left - 1;
                result = Math.max(height[cur] * width, result);
            }
            stack.push(i);
        }

        while (!stack.isEmpty()){
            int cur = stack.pop();
            // 弹出的元素高度非 0 ，则用 高度 * 宽度，计算面积
            int left = stack.isEmpty() ? -1 :stack.peek();
            int width = height.length - left - 1;
            result = Math.max(height[cur] * width, result);
        }

        return result;
    }


}

