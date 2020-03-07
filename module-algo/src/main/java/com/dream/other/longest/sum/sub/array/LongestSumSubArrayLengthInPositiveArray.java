package com.dream.other.longest.sum.sub.array;


import java.io.*;

/**
 * @author fanrui
 * 未排序正数数组中累加和为给定值的最长子数组的长度
 */
public class LongestSumSubArrayLengthInPositiveArray {


    public static void main(String[] args) throws Exception {
        //准备数据
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] str1 = in.readLine().split(" ");
        int N = Integer.parseInt(str1[0]);
        int k = Integer.parseInt(str1[1]);
        String[] str2 = in.readLine().split(" ");
        int[] num = new int[N];
        for (int i = 0; i < N; i++) {
            num[i] = Integer.parseInt(str2[i]);
        }
        int res = maxLength(num, k);
        System.out.println(res);
    }

    public static int maxLength(int[] nums, int aim) {
        int res = 0;
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int curWindowSum = nums[0];
        int L = 0;
        int R = 0;

        while (R < nums.length) {
            if (curWindowSum < aim) {
                if (R == nums.length - 1) {
                    break;
                }
                curWindowSum += nums[++R];
            } else if (curWindowSum > aim) {
                curWindowSum -= nums[L++];
            } else {
                res = Math.max(res, R - L + 1);
                if (R == nums.length - 1) {
                    break;
                }
                curWindowSum += nums[++R];
                curWindowSum -= nums[L++];
            }
        }

        return res;
    }
}