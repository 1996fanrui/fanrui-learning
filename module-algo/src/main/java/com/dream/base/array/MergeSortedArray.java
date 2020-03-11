package com.dream.base.array;

/**
 * @author fanrui
 * 合并有序数组
 * LeetCode ：https://leetcode-cn.com/problems/sorted-merge-lcci/
 */
public class MergeSortedArray {
    public void merge(int[] A, int m, int[] B, int n) {

        if (B == null || n == 0 || B.length == 0) {
            return;
        }

        int aIndex = m - 1;
        int bIndex = n - 1;
        int allIndex = m + n - 1;

        while (aIndex >= 0 && bIndex >= 0) {
            if (A[aIndex] > B[bIndex]) {
                A[allIndex--] = A[aIndex--];
            } else {
                A[allIndex--] = B[bIndex--];
            }
        }

        // 注： 这里时 <= ，而不是 小于
        for (int i = 0; i <= bIndex; i++) {
            A[i] = B[i];
        }
    }
}
