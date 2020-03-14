package com.dream.tree.unionfind;


/**
 * @author fanrui
 * @time 2020-03-15 00:54:18
 * 岛屿的最大面积
 * 染色法实现
 */
public class MaxAreaOfIsland {


    public int maxAreaOfIsland(int[][] grid) {

        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }
        int maxArea = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    maxArea = Math.max(maxArea, infect(grid, i, j));
                }
            }
        }
        return maxArea;
    }


    private int infect(int[][] grid, int i, int j) {
        if (i < 0 || j < 0
                || i >= grid.length || j >= grid[0].length
                || grid[i][j] != 1) {
            return 0;
        }
        grid[i][j] = 2;
        int res = 1;

        res += infect(grid, i + 1, j);
        res += infect(grid, i - 1, j);
        res += infect(grid, i, j + 1);
        res += infect(grid, i, j - 1);

        return res;
    }

}
