package com.dream.math;

/**
 * @author fanrui
 * 69. x 的平方根
 */
public class Sqrt {

    public int mySqrt(int x) {

        if (x <= 1) {
            return x;
        }

        int L = 1;
        // R 取 x 有 46340 的最小值
        int R = Math.min(46340, x);

        int result = 0;
        while (L <= R) {
            int mid = L + ((R - L) >> 1);
            if (mid == x / mid) {
                return mid;
            } else if (mid * mid > x) {
                // mid 大了，R 减小
                R = mid - 1;
            } else {
                // mid 小了，L 增大
                L = mid + 1;
                result = mid;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        new Sqrt().mySqrt(2147395600);
    }

}
