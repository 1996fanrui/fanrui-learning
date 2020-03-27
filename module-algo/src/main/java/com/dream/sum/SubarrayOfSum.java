package com.dream.sum;

import java.util.LinkedList;

/**
 * @author fanrui
 * 和为 s 的连续正数序列
 * 剑指 57-II： https://leetcode-cn.com/problems/he-wei-sde-lian-xu-zheng-shu-xu-lie-lcof/
 */
public class SubarrayOfSum {

    public int[][] findContinuousSequence(int target) {

        if (target <= 2) {
            return new int[0][0];
        }
        int left = 1;
        int right = 2;
        int curSum = 3;
        int mid = (target + 1) / 2;

        LinkedList<int[]> list = new LinkedList<>();

        while (left < mid) {
            // 如果窗口内 sum 小于 target， right 右移。
            if (curSum < target) {
                right++;
                curSum += right;
            } else if (curSum > target) {
                // 如果窗口内 sum 大于 target， left 右移。
                curSum -= left;
                left++;
            } else {
                // 如果窗口内sum 等于 target，找到匹配结果，然后 left 和 right 都右移。
                int length = right - left + 1;
                int[] tmp = new int[length];
                for (int i = 0; i < length; i++) {
                    tmp[i] = i + left;
                }
                list.add(tmp);
                curSum -= left;
                left++;
                right++;
                curSum += right;
            }
        }

        int size = list.size();
        int[][] res = new int[size][0];
        for (int i = 0; i < size; i++) {
            res[i] = list.pollFirst();
        }
        return res;
    }

    public static void main(String[] args) {
        SubarrayOfSum subarrayOfSum = new SubarrayOfSum();
        System.out.println(subarrayOfSum.findContinuousSequence(9));
    }

}
