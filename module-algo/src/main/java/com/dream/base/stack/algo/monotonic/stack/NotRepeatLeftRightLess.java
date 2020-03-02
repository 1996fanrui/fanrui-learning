package com.dream.base.stack.algo.monotonic.stack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

/**
 * @author fanrui
 * 给定一个不含有重复值的数组 arr，找到每一个 i 位置左边和右边离 i 位置最近且值比 arr[i] 小的位置。返回所有位置相应的信息。
 * https://www.nowcoder.com/practice/e3d18ffab9c543da8704ede8da578b55
 *
 */
public class NotRepeatLeftRightLess {
    /**
     * 没有重复数字时使用单调栈结构，时间复杂度O(N)
     */
    public static int[][] getNearLessNoRepeat(int[] arr) {
        // 排除两种特例：null 空数组[]
        if (arr == null || arr.length < 1) {
            return null;
        }
        int[][] ret = new int[arr.length][2];
        // 单调栈，栈底到栈顶，由小到大
        Stack<Integer> stack = new Stack<>();
        for(int i = 0; i < arr.length; i++){
            // 将栈中大于当前元素的全部弹出
            while(!stack.isEmpty() && arr[i] < arr[stack.peek()]){
                int cur = stack.pop();
                // cur 右边小的最近的是 i
                ret[cur][1] = i;
                // cur 左边小的最近的是 null，或者栈中下一个元素
                ret[cur][0] = stack.isEmpty() ? -1 : stack.peek();
            }
            stack.push(i);
        }
        while(!stack.isEmpty()){
            int cur = stack.pop();
            // cur 右边小的最近的是 null
            ret[cur][1] = -1;
            // cur 左边小的最近的是 null，或者栈中下一个元素
            ret[cur][0] = stack.isEmpty() ? -1 : stack.peek();
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.valueOf(bufferedReader.readLine());
        int[] arr = new int[n];
        String[] numbers = bufferedReader.readLine().split(" ");
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.valueOf(numbers[i]);
        }
        int[][] res = getNearLessNoRepeat(arr);
        for (int i = 0; i < res.length; i++) {
            System.out.println(res[i][0] + " " + res[i][1]);
        }
    }
}
