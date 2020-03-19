package com.dream.serach.dbfs;

/**
 * @author fanrui
 * @time 2020-03-19 12:24:26
 * 矩阵中单词搜索
 * LeetCode 79：https://leetcode-cn.com/problems/word-search/
 * 剑指 Offer 12：https://leetcode-cn.com/problems/ju-zhen-zhong-de-lu-jing-lcof/
 */
public class WordSearch {

    // 总行数和总列数
    private int rowCount;
    private int colCount;

    public boolean exist(char[][] board, String word) {
        if (board == null || board.length == 0 || board[0].length == 0 || word == null) {
            return false;
        }
        if (word.length() == 0) {
            return true;
        }
        rowCount = board.length;
        colCount = board[0].length;

        boolean[][] visited = new boolean[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                if (dfs(board, word, 0, i, j, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int curIndex
            , int curRow, int curCol
            , boolean[][] visited) {

        // 当前位置越界，或当前位置已经被访问过，
        // 或当前位置的字符与要求不符，直接返回 false
        if (curRow >= rowCount || curRow < 0 ||
                curCol >= colCount || curCol < 0 ||
                visited[curRow][curCol] ||
                board[curRow][curCol] != word.charAt(curIndex)) {
            return false;
        }

        // 当前 index 已经遍历完单词了，所以返回 true
        if (curIndex == word.length() - 1) {
            return true;
        }

        // 将当前位置标记为已访问
        visited[curRow][curCol] = true;
        boolean res = false;
        // 往四个方向去遍历，只要有一个方向满足要求，就返回 true
        if (dfs(board, word, curIndex + 1, curRow - 1, curCol, visited) ||
                dfs(board, word, curIndex + 1, curRow + 1, curCol, visited) ||
                dfs(board, word, curIndex + 1, curRow, curCol - 1, visited) ||
                dfs(board, word, curIndex + 1, curRow, curCol + 1, visited)) {
            return true;
        }
        // 所有路径都不同，则请求访问记录，并返回 false
        visited[curRow][curCol] = false;
        return false;
    }

}
