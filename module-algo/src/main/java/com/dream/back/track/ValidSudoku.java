package com.dream.back.track;

/**
 * @author fanrui
 * @time 2020-03-14 23:59:17
 * 判断数独是否有效
 * LeetCode 36：https://leetcode-cn.com/problems/valid-sudoku/
 */
public class ValidSudoku {

    public boolean isValidSudoku(char[][] board) {
        if (board == null || board.length != 9
                || board[0] == null || board[0].length != 9) {
            return false;
        }

        boolean[][] row = new boolean[9][9];
        boolean[][] col = new boolean[9][9];
        boolean[][] box = new boolean[9][9];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == '.') {
                    continue;
                }
                int num = board[r][c] - '1';
                int boxIndex = (r / 3) * 3 + c / 3;
                if (row[r][num] || col[c][num] || box[boxIndex][num]) {
                    return false;
                }
                row[r][num] = col[c][num] = box[boxIndex][num] = true;
            }
        }

        return true;
    }

}
