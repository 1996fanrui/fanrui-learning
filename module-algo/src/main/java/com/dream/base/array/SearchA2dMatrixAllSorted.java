package com.dream.base.array;


/**
 * @author fanrui
 * @time 2020-03-18 01:19:41
 * 搜索二维矩阵（矩阵中可以当做是全排序的）
 * LeetCode 74：https://leetcode-cn.com/problems/search-a-2d-matrix/
 */
public class SearchA2dMatrixAllSorted {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }
        int COL = matrix[0].length;
        int L = 0;
        int R = COL * matrix.length - 1;
        while (L <= R) {
            // 计算 mid 及 mid 对应的 行和列，与 target 比较大小
            int mid = L + ((R - L) >> 1);
            int midRow = mid / COL;
            int midCol = mid % COL;
            if(matrix[midRow][midCol] == target){
                return true;
            } else if(matrix[midRow][midCol] < target){
                L = mid + 1;
            } else {
                R = mid - 1;
            }
        }
        return false;
    }

}
