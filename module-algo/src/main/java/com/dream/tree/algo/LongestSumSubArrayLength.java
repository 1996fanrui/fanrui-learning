package com.dream.tree.algo;

import java.util.*;
import java.io.*;

/**
 * @author fanrui
 * 未排序数组中累加和为给定值的最长子数组长度
 * 牛客链接：https://www.nowcoder.com/practice/36fb0fd3c656480c92b569258a1223d5?tpId=101&tqId=33077&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 */
public class LongestSumSubArrayLength {


    public static int maxLength(int[] arr, int aim){
        int res = 0;
        if(arr == null || arr.length == 0){
            return res;
        }
        // LeetCode 刷运行时间的小技巧，HashMap 指定容量，减少扩容代价
        HashMap<Integer, Integer> map = new HashMap<>(arr.length);
        map.put(0, -1);

        int sum = 0;
        for (int i = 0; i < arr.length; i++){
            sum += arr[i];
            if(map.containsKey(sum - aim)){
                res = Math.max(res,i - map.get(sum - aim));
            }
            if(!map.containsKey(sum)){
                map.put(sum, i);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String[] first = reader.readLine().split(" ");
            String[] data = reader.readLine().split(" ");
            int n = Integer.parseInt(first[0]);
            int k = Integer.parseInt(first[1]);
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = Integer.parseInt(data[i]);
            }
            System.out.println(maxLength(arr, k));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
