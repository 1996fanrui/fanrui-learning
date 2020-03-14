package com.dream.back.track;


/**
 * @author fanrui
 * @time 2020-03-14 11:12:04
 * N 皇后的方案数量
 * LeetCode 52：https://leetcode-cn.com/problems/n-queens-ii/
 */
public class NumOfNQueens {

    int res = 0;

    public int totalNQueens(int n) {
        if (n < 1) {
            return 0;
        }
        this.res = 0;
        recNumQueens(n, 0, new boolean[n], new boolean[2 * n], new boolean[2 * n]);
        return res;
    }

    private void recNumQueens(int n, int curRow, boolean[] col, boolean[] pie, boolean[] na) {
        if (n == curRow) {
            res++;
            return;
        }

        // 遍历第 curRow 行，所有的列，是否符合规则，并继续遍历
        for (int i = 0; i < n; i++) {
            if (!col[i] && !pie[curRow + i] && !na[curRow - i + n]) {
                col[i] = true;
                pie[curRow + i] = true;
                na[curRow - i + n] = true;
                recNumQueens(n, curRow + 1, col, pie, na);
                col[i] = false;
                pie[curRow + i] = false;
                na[curRow - i + n] = false;
            }
        }
    }

}
