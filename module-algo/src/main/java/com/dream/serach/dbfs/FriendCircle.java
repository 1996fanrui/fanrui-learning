package com.dream.serach.dbfs;

import java.util.LinkedList;

/**
 * @author fanrui
 * @time 2020-03-17 21:50:03
 * LeetCode 547：https://leetcode-cn.com/problems/friend-circles/
 */
public class FriendCircle {

    // 思路一：dfs
    public int findCircleNum1(int[][] M) {
        if (M == null || M.length == 0) {
            return 0;
        }

        boolean[] visited = new boolean[M.length];

        int count = 0;
        for (int i = 0; i < M.length; i++) {
            // 没有被访问过则遍历
            if (!visited[i]) {
                dfs(M, i, visited);
                count++;
            }
        }
        return count;
    }

    private void dfs(int[][] M, int cur, boolean[] visited) {
        visited[cur] = true;
        for (int j = 0; j < M.length; j++) {
            // cur 元素与 j 元素是朋友，且 j 元素还未被访问过
            if (M[cur][j] == 1 && !visited[j]) {
                dfs(M, j, visited);
            }
        }
    }


    // 思路二：bfs
    public int findCircleNum(int[][] M) {
        if (M == null || M.length == 0) {
            return 0;
        }
        boolean[] visited = new boolean[M.length];
        LinkedList<Integer> queue = new LinkedList<>();
        int count = 0;
        for (int i = 0; i < M.length; i++) {
            // 没有被访问过则遍历
            if (!visited[i]) {
                queue.add(i);
                while (!queue.isEmpty()) {
                    int cur = queue.pollFirst();
                    visited[cur] = true;
                    for (int j = 0; j < M.length; j++) {
                        if (M[cur][j] == 1 && !visited[j]) {
                            queue.add(j);
                        }
                    }
                }
                count++;
            }
        }
        return count;
    }


}
