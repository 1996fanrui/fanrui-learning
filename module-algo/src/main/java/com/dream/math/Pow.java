package com.dream.math;

public class Pow {

    // 思路一：暴力，x 循环相乘 n 次即可，如果 n 为 负数，需要将 x 换成 1/x 即可。
    // 会超时
    public double myPowForce(double x, int n) {
        if (n == 0) {
            return 1;
        } else if (n < 0) {
            x = 1 / x;
            n = -n;
        }
        double res = x;
        for (int i = 1; i < n; i++) {
            res *= x;
        }
        return res;
    }


    // 思路二： 加速
    public double myPow(double x, int n) {
        return recPow(x,n);
    }

    private double recPow(double x, long n) {
        if (n == 0) {
            return 1;
        } else if (n < 0) {
            x = 1 / x;
            n = -n;
        }

        // 偶数
        if ((n & 1) == 0) {
            return recPow(x * x, n >> 1);
        } else {
            return x * recPow(x, n - 1);
        }
    }

}
