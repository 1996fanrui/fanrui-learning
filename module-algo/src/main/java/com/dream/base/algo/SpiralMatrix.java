package com.dream.base.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanrui
 * @time 2020-03-20 20:23:28
 * 转圈打印矩阵（螺旋矩阵）
 * 剑指 Offer 29：https://leetcode-cn.com/problems/shun-shi-zhen-da-yin-ju-zhen-lcof/
 * LeetCode 54：https://leetcode-cn.com/problems/spiral-matrix/
 */
public class SpiralMatrix {

    public List<Integer> spiralOrder(int[][] matrix) {
        ArrayList<Integer> res = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return res;
        }

        int left = 0;
        int right = matrix[0].length - 1;
        int up = 0;
        int down = matrix.length - 1;

        while (left <= right && up <= down) {
            // 从左上角往右上角走
            for (int i = left; i <= right; i++) {
                res.add(matrix[up][i]);
            }
            // 上边界大于 下边界，直接结束
            if (++up > down) {
                break;
            }

            // 从右上角往右下角走
            for (int i = up; i <= down; i++) {
                res.add(matrix[i][right]);
            }
            // 右边界小于 左边界，直接结束
            if (--right < left) {
                break;
            }

            // 从右下角往左下角走
            for (int i = right; i >= left; i--) {
                res.add(matrix[down][i]);
            }
            // 下边界小于 上边界，直接结束
            if (--down < up) {
                break;
            }

            // 从左下角往左上角走
            for (int i = down; i >= up; i--) {
                res.add(matrix[i][left]);
            }
            // 左边界大于 右边界，直接结束
            if (++left > right) {
                break;
            }
        }
        return res;
    }
}
