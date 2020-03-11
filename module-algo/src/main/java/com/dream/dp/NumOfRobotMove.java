package com.dream.dp;

import java.io.*;

/**
 * @author fanrui
 * 机器人到达指定位置方法数
 * 牛客题目:https://www.nowcoder.com/practice/54679e44604f44d48d1bcadb1fe6eb61?tpId=101&tqId=33085&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 */
public class NumOfRobotMove {


    /**
     * @param n 移动范围
     * @param m 当前位置
     * @param k 剩余步数
     * @param p 目标位置
     * @return 所有移动方案的总数
     */
    public static final int getWays(int n, int m, int k, int p) {
        int[] dp = new int[n];
        dp[m - 1] = 1;
        for (int i = 1, len = k + 1; i < len; i++) {
            // leftTemp 用于存储 dp[j - 1]
            int leftTemp = 0;
            for (int j = 0; j < n; j++) {
                int temp = dp[j];
                if (j == 0) {
                    dp[j] = dp[j + 1];
                } else if (j == n - 1) {
                    dp[j] = leftTemp;
                } else {
                    dp[j] = (leftTemp + dp[j + 1]);
                    dp[j] %= 1000000007;
                }
                leftTemp = temp;
            }
        }
        return dp[p - 1];
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] strs = br.readLine().split(" ");
        int n = Integer.parseInt(strs[0]);
        int m = Integer.parseInt(strs[1]);
        int k = Integer.parseInt(strs[2]);
        int p = Integer.parseInt(strs[3]);
        System.out.println(getWays(n, m, k, p));
    }


}
