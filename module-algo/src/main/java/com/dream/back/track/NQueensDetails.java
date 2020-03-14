package com.dream.back.track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanrui
 * @time 2020-03-14 11:51:45
 * N 皇后的具体方案
 * LeetCode 51：https://leetcode-cn.com/problems/n-queens/
 */
public class NQueensDetails {

    List<List<String>> res;

    public List<List<String>> solveNQueens(int n) {
        res = new ArrayList<>();
        if (n < 1) {
            return res;
        }
        recNumQueens(new int[n], n, 0, new boolean[n], new boolean[2 * n], new boolean[2 * n]);
        return res;
    }

    private void recNumQueens(int[] path, int n, int curRow, boolean[] col, boolean[] pie, boolean[] na) {
        if (n == curRow) {
            res.add(genPathString(n,path));
            return;
        }

        // 遍历第 curRow 行，所有的列，是否符合规则，并继续遍历
        for (int i = 0; i < n; i++) {
            if (!col[i] && !pie[curRow + i] && !na[curRow - i + n]) {
                col[i] = true;
                pie[curRow + i] = true;
                na[curRow - i + n] = true;
                path[curRow] = i;
                recNumQueens(path, n, curRow + 1, col, pie, na);
                col[i] = false;
                pie[curRow + i] = false;
                na[curRow - i + n] = false;
            }
        }
    }

    private List<String> genPathString(int n, int[] path){
        char[] chars = new char[n];
        List<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(path[i] == j){
                    chars[j] = 'Q';
                } else {
                    chars[j] = '.';
                }
            }
            list.add(new String(chars));
        }
        return list;
    }

}
