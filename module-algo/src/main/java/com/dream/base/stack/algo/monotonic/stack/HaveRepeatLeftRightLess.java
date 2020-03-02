package com.dream.base.stack.algo.monotonic.stack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author fanrui
 * 给定一个可能含有重复值的数组 arr，找到每一个 i 位置左边和右边离 i 位置最近且值比 arr[i] 小的位置。返回所有位置相应的信息。
 * https://www.nowcoder.com/practice/2a2c00e7a88a498693568cef63a4b7bb
 *
 * 如果有相同的元素，则先暂时放到 临时 list 中，最后一块处理这些相同元素
 */
public class HaveRepeatLeftRightLess {
    /**
     * 没有重复数字时使用单调栈结构，时间复杂度O(N)
     */
    public static int[][] getNearLess(int[] arr) {
        // 排除两种特例：null 空数组[]
        if (arr == null || arr.length < 1) {
            return null;
        }
        int[][] ret = new int[arr.length][2];
        // 单调栈，栈底到栈顶，由小到大
        Stack<Integer> stack = new Stack<>();
        LinkedList<Integer> list = new LinkedList<>();

        for(int i = 0; i < arr.length; i++) {
            // 将栈中大于当前元素的全部弹出
            while(!stack.isEmpty() && arr[i] < arr[stack.peek()]){
                int cur = stack.pop();
                list.add(cur);
                // 相同的元素先暂时放到 临时 list 中
                while (!stack.isEmpty() && arr[cur] == arr[stack.peek()]){
                    list.add(stack.pop());
                }

                // 临时栈中的所有元素相同，且 左右都一样
                int left = stack.isEmpty() ? -1 : stack.peek();
                do {
                    cur = list.removeFirst();
                    // cur 右边小的最近的是 i
                    ret[cur][1] = i;
                    // cur 左边小的最近的是 null，或者栈中下一个元素
                    ret[cur][0] = left;
                } while (!list.isEmpty());
            }
            stack.push(i);
        }

        while(!stack.isEmpty()) {
            int cur = stack.pop();
            list.add(cur);
            // 相同的元素先暂时放到 临时栈中
            while (!stack.isEmpty() && arr[cur] == arr[stack.peek()]){
                list.add(stack.pop());
            }

            // 临时栈中的所有元素相同，且 左右都一样
            int left = stack.isEmpty() ? -1 : stack.peek();
            do {
                cur = list.removeFirst();
                // cur 右边小的最近的是 null
                ret[cur][1] = -1;
                // cur 左边小的最近的是 null，或者栈中下一个元素
                ret[cur][0] = left;
            } while (!list.isEmpty());
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.valueOf(bufferedReader.readLine());
        int[] arr = new int[n];
        String[] numbers = bufferedReader.readLine().split(" ");
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(numbers[i]);
        }
        int[][] res = getNearLess(arr);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < res.length-1; i++) {
            sb.append(res[i][0]).append(" ").append(res[i][1]).append("\n");
        }
        sb.append(res[res.length-1][0]).append(" ").append(res[res.length-1][1]);
        System.out.println(sb.toString());
        bufferedReader.close();
    }
}
