package com.dream.tree.unionfind.Island;

import java.util.Arrays;

public class InfectColorBFS {

    public static int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 ) {
            return 0;
        }
        //  二维数组的行数
        int R = grid.length;
        //  二维数组的列数
        int C = grid[0].length;
        int res = 0;
        // 遍历所有元素
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                // 发现元素是 1
                if (grid[r][c] == 1) {
                    // 岛数量 +1
                    res++;
                    // 染色
                    infect(grid, r, c, R, C);
                }
            }
        }
        return res;
    }

    // 四个方向都需要染色
    private static void infect(char[][] m, int i, int j, int N, int M) {
        if (i < 0 || i >= N || j < 0 || j >= M || m[i][j] != 1) {
            return;
        }
        // 将自己所在节点染色
        m[i][j] = 2;
        // 递归遍历上下左右四个方向进行染色
        infect(m, i + 1, j, N, M);
        infect(m, i - 1, j, N, M);
        infect(m, i, j + 1, N, M);
        infect(m, i, j - 1, N, M);
    }

    public static void main(String[] args) {
        char[][] m1 = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 1, 1, 1, 0, 1, 1, 1, 0 },
                        { 0, 1, 1, 1, 0, 0, 0, 1, 0 },
                        { 0, 1, 1, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };
        System.out.println(numIslands(m1));
        System.out.println(Arrays.deepToString(m1));

        char[][] m2 = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
                { 0, 1, 1, 1, 0, 0, 0, 1, 0 },
                { 0, 1, 1, 0, 0, 0, 1, 1, 0 },
                { 0, 0, 0, 0, 0, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };
        System.out.println(numIslands(m2));

        char[][] m3 = {};
        System.out.println(numIslands(m3));

    }

}
