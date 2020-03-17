package com.dream.math;

/**
 * @author fanrui
 * @time 2020-03-18 02:04:52
 * 矩阵重叠
 * LeetCode 863：https://leetcode-cn.com/problems/rectangle-overlap/
 */
public class IsRectangleOverlap {

    // 思路一：如果不重叠，那么两个矩形位置肯定是一左一右，或者一上一下
    public boolean isRectangleOverlap1(int[] rec1, int[] rec2) {
        return !(rec1[2] <= rec2[0] || rec2[2] <= rec1[0]   // 一左一右
                || rec1[3] <= rec2[1] || rec2[3] <= rec1[1]);   // 一上一下
    }

    // 思路二：如果两个矩形重叠，那么它们重叠的区域一定也是一个矩形，
    public boolean isRectangleOverlap(int[] rec1, int[] rec2) {
        return (Math.min(rec1[2], rec2[2]) > Math.max(rec1[0], rec2[0])     // x 轴有重叠
                && Math.min(rec1[3], rec2[3]) > Math.max(rec1[1], rec2[1]));    // y 轴有重叠
    }

}
