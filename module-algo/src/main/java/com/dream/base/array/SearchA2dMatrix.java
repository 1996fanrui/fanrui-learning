package com.dream.base.array;


/**
 * @author fanrui
 * @time 2020-03-18 01:10:21
 * 在行列都排好序的矩阵中找数（搜索二维矩阵 II）
 * LeetCode 240：https://leetcode-cn.com/problems/search-a-2d-matrix-ii/
 * 剑指 Offer 4：https://leetcode-cn.com/problems/er-wei-shu-zu-zhong-de-cha-zhao-lcof/
 */
public class SearchA2dMatrix {

    public boolean searchMatrix(int[][] matrix, int target) {

        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int curRow = 0;
        int curCol = matrix[0].length - 1;
        while (curRow < matrix.length && curCol >= 0){
            if(matrix[curRow][curCol] == target){
                return true;
            } else if(matrix[curRow][curCol] > target){
                // 当前元素大于要查找的元素，向左走
                curCol--;
            } else {
                // 当前元素小于要查找的元素，向下走
                curRow++;
            }
        }
        return false;
    }

}
