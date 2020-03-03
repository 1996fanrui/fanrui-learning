package com.dream.base.stack.algo.monotonic.stack;

import java.util.*;
import java.io.*;

/**
 * @author fanrui
 * 可见山峰对数量
 * 牛客链接：https://www.nowcoder.com/practice/16d1047e9fa54cea8b5170b156d89e38?tpId=101&tqId=33173&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 * 相同元素的处理方案：
 *      该实现方案，当元素相等时都放到 stack 中，pop 时，计算 到底有多少个，
 *      左神的实现：将所有的元素封装到 Pair 对象中，对象包含元素的 value 值和 次数，然后放到 stack 中
 * 找最大元素，然后按照环遍历的方案：
 *      该实现方案，从 maxIndex 到 length + maxIndex。
 *      左神的实现，从 maxIndex 到 maxIndex，搞了一个方法去专门对 index值 +1，会循环
 */
public class VisiblePeakPair{


    public static void main(String[] args){
        int[] arr = null;
        try(BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))){
            int len = Integer.valueOf(bf.readLine());
            arr = new int[len];
            String[] str = bf.readLine().split(" ");
            for(int i = 0; i < len; i++){
                arr[i] = Integer.valueOf(str[i]);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(getVisiblePeakPair(arr));
    }

    private static int getVisiblePeakPair(int[] arr) {

        if(arr == null || arr.length == 0){
            return 0;
        }

        int peakPair = 0;

        // 最大数的 index
        int maxIndex = 0;
        // 最大的值的个数
        int maxValueCount = 1;
        for (int i = 1; i < arr.length; i++) {
            if(arr[i] > arr[maxIndex]){
                maxIndex = i;
            } else if(arr[i] == arr[maxIndex]){
                maxValueCount++;
            }
        }

        // 小数找大数，单调递减的栈
        Stack<Integer> stack = new Stack<>();
        int curCount;

        for (int i = maxIndex; i < arr.length + maxIndex; i++) {
            // 当前元素大于栈中元素，则弹出
            while (!stack.isEmpty() && arr[i%arr.length] > arr[stack.peek()]){
                int cur = stack.pop();
                curCount = 1;
                // 需要一下子把相等的元素都弹出来
                while (arr[cur] == arr[stack.peek()]){
                    stack.pop();
                    curCount++;
                }
                peakPair += getInternalSum(curCount);
                peakPair += curCount * 2;
            }
            stack.push(i%arr.length);
        }

        // stack 非空，且 stack 中除了最大值还有其他元素
        while (!stack.isEmpty() && arr[maxIndex] != arr[stack.peek()]){
            int cur = stack.pop();
            curCount = 1;
            // 需要一下子把相等的元素都弹出来
            while (arr[cur] == arr[stack.peek()]){
                stack.pop();
                curCount++;
            }
            peakPair += getInternalSum(curCount);
            // stack 中仅剩最大值 且 maxValueCount == 1
            if(maxValueCount == 1 && arr[maxIndex] == arr[stack.peek()]){
                peakPair += curCount;
            } else {
                peakPair += curCount * 2;
            }
        }

        // 最大值之间还有一个组合关系
        peakPair += getInternalSum(maxValueCount);

        return peakPair;
    }


    /**
     * 求 C 2 k，当前题目只求 C 2 k，所以没必要使用下面的 combine 方法
     *
     */
    public static int getInternalSum(int k){
        return k == 1 ? 0 : k * (k-1)/2;
    }

    /**
     * 从 n 个数里选出 m 个数，有多少种组合关系
     * Cmn  = n! / m!(n-m)!
     *      = n∗(n−1)∗...∗(n−m+1)/ m∗(m−1)∗...∗1
     * @param m
     * @param n
     * @return
     */
    public static int combine(int m, int n){
        if(m > n){
            throw new IllegalArgumentException("逗我呢？");
        } else if (m==n){
            return 1;
        }
        int up = 1;
        int down = 1;
        for (int i = 1; i <= m; i++) {
            down *= i;
            up *= n - i + 1;
        }
        return up/down;
    }



}
