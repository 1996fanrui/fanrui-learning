package com.dream.math;

/**
 * @author fanrui
 * @time 2020-04-05 13:02:48
 * 面试题66. 构建乘积数组
 * 剑指 66： https://leetcode-cn.com/problems/gou-jian-cheng-ji-shu-zu-lcof/
 */
public class ConstructMultiArr {

    public int[] constructArr(int[] a) {
        if (a == null || a.length == 0) {
            return a;
        }
        // C[i] = A[0]×A[1]×…×A[i-1]
        int[] c = new int[a.length];
        // D[i] = A[i+1]×…×A[n-1]
        int[] d = new int[a.length];
        c[0] = 1;
        d[a.length - 1] = 1;
        for (int i = 1; i < a.length; i++) {
            c[i] = c[i - 1] * a[i - 1];
            d[a.length - 1 - i] = d[a.length - i] * a[a.length - i];
        }
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = c[i] * d[i];
        }
        return res;
    }

}
