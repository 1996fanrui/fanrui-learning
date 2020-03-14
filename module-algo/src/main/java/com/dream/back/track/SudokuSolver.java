package com.dream.back.track;

/**
 * @author fanrui
 * @time 2020-03-15 00:32:56
 * 37. 解数独
 * LeetCode 37：https://leetcode-cn.com/problems/sudoku-solver/
 *
 */
public class SudokuSolver {

    public void solveSudoku(char[][] board) {
        if (board == null || board.length != 9
                || board[0] == null || board[0].length != 9) {
            return;
        }

        boolean[][] row = new boolean[9][9];
        boolean[][] col = new boolean[9][9];
        boolean[][] box = new boolean[9][9];

        // 初始化三个数组
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == '.') {
                    continue;
                }
                int num = board[r][c] - '1';
                int boxIndex = (r / 3) * 3 + c / 3;
                row[r][num] = col[c][num] = box[boxIndex][num] = true;
            }
        }

        solve(board, 0, 0, row, col, box);
    }


    private boolean solve(char[][] board, int curRow, int curCol,
                          boolean[][] row, boolean[][] col, boolean[][] box) {
        // curRow 用于加速，curRow 之前的数据不需要再遍历.
        // curColumn 也是同理
        for (int r = curRow; r < 9; r++) {
            for (int c = curCol; c < 9; c++) {
                if (c == 8) {
                    curRow++;
                    curCol = 0;
                } else {
                    curCol++;
                }
                if (board[r][c] != '.') {
                    continue;
                }
                int boxIndex = (r / 3) * 3 + c / 3;
                for (int num = 0; num < 9; num++) {
                    // 如果存在一个为 true，表示当前的 num 不合法，所以跳出
                    if (row[r][num] || col[c][num] || box[boxIndex][num]) {
                        continue;
                    }
                    row[r][num] = col[c][num] = box[boxIndex][num] = true;
                    board[r][c] = (char) ('1' + num);
                    boolean isSolve = solve(board, curRow, curCol, row, col, box);
                    if (isSolve) {
                        return true;
                    }
                    // 递归失败，则 三个数组清零，且 真实数据存储为 '.'
                    row[r][num] = col[c][num] = box[boxIndex][num] = false;
                    board[r][c] = '.';
                }
                // 当前位置试了 9 种情况都不满足，返回 false
                return false;
            }
        }
        // 如果当前位置
        return true;
    }

}
