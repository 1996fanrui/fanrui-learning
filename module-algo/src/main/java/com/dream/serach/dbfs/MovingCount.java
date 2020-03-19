package com.dream.serach.dbfs;

/**
 * @author fanrui
 * @time 2020-03-19 19:23:58
 * 机器人的运动范围：机器人能够到达多少个格子
 * 剑指 Offer 13：https://leetcode-cn.com/problems/ji-qi-ren-de-yun-dong-fan-wei-lcof/
 */
public class MovingCount {

    public int movingCount(int m, int n, int k) {
        boolean[][] visited = new boolean[m][n];
        return dfs(visited, m, n, 0, 0, k);
    }

    private int dfs(boolean[][] visited, int m, int n,
                    int curRow, int curCol, int k){
        // 越界数据处理
        if( curRow < 0 || curRow >= m
                || curCol < 0 || curCol >= n){
            return 0;
        }
        // 当前节点已经访问过了，直接返回 0
        if(visited[curRow][curCol]){
            return 0;
        }
        int res = 0;
        visited[curRow][curCol] = true;
        // 当前节点可以进入
        if(check(curRow, curCol, k)){
            res = 1;
            res += dfs(visited, m, n, curRow+1, curCol, k);
            res += dfs(visited, m, n, curRow-1, curCol, k);
            res += dfs(visited, m, n, curRow, curCol-1, k);
            res += dfs(visited, m, n, curRow, curCol+1, k);
        }
        return res;
    }

    // 检查当前节点是否可以进入
    private boolean check(int curRow, int curCol, int k){
        int pointSum = 0;
        while(curRow !=0){
            pointSum += curRow % 10;
            curRow /= 10;
        }
        while(curCol !=0){
            pointSum += curCol % 10;
            curCol /= 10;
        }
        return pointSum <= k;
    }
}
