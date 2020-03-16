package com.dream.dp;


/**
 * @author fanrui
 * 263. 丑数
 * LeetCode 263： https://leetcode-cn.com/problems/ugly-number/
 */
public class UglyNumber {

    // 思路一：利用质因数的特点
    public boolean isUgly1(int num) {

        if (num <= 0) {
            return false;
        }

        while ((num & 1) == 0) {
            num = num >> 1;
        }

        while ((num % 3) == 0) {
            num = num / 3;
        }

        while ((num % 5) == 0) {
            num = num / 5;
        }
        return num == 1;
    }


    /**
     * 如果题目变了：判断给定的数，是否能被 3、4、 20 整除
     * 上面的解法就不行了。给定 60，先用 3 整除，得到 20 ，再用 4 整除，得到 5，最后没法整除了
     * 但实际上 60 能被整除，因为 3*20 = 60。
     * 数字再变化，也可以使用 dp 思想来解决。
     * <p>
     * 思路二：dp 思想(但是超内存限制了，没法优化。。。)
     * 要求被 2、3、5 整除，给定 60，我们可以认为数字 60 可以由 60/2=30、60/3=20、60/5=12 这三个数递推得到
     * 30 也可以由 30/2=15、30/3=10、30/5=6 得到
     * 所以 boolean 类型的 dp 数组的状态方程为 dp[n] = dp[n/2] || dp[n/3] || dp[n/5]。这里要求必须能够整除
     */
    public boolean isUgly2(int num) {

        if (num <= 0) {
            return false;
        }

        boolean[] dp = new boolean[num + 1];

        dp[1] = true;

        for (int i = 2; i <= num; i++) {
            if ((i & 1) == 0) {
                dp[i] = dp[i] || dp[i >> 1];
            }

            if ((i % 3) == 0) {
                dp[i] = dp[i] || dp[i / 3];
            }

            if ((i % 5) == 0) {
                dp[i] = dp[i] || dp[i / 5];
            }
        }
        return dp[num];
    }


}
