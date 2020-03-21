package com.dream.math;

/**
 * @author fanrui
 * @time 2020-03-21 17:14:42
 * 1～n整数中1出现的次数
 * LeetCode 233： https://leetcode-cn.com/problems/number-of-digit-one/
 * 剑指 Offer 43：https://leetcode-cn.com/problems/1nzheng-shu-zhong-1chu-xian-de-ci-shu-lcof/
 */
public class NumberOfDigitOne {

    public int countDigitOne(int n) {

        int count = 0;
        for (long k = 1; k <= n; k *= 10) {
            //  当前计算的 位，后续的余数
            long abc = n % k;
            long xyzd = n / k;
            // 当前统计的位 上的值
            long d = xyzd % 10;
            long xyz = xyzd / 10;
            count += xyz * k;

            if (d == 1) {
                count += abc + 1;
            } else if (d > 1) {
                count += k;
            }
        }
        return count;
    }

}
