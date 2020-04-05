package com.dream.math;

/**
 * @author fanrui
 * @time 2020-04-05 11:35:29
 * 字符串转数字
 * LeetCode 8： https://leetcode-cn.com/problems/string-to-integer-atoi/
 * 剑指 67： https://leetcode-cn.com/problems/ba-zi-fu-chuan-zhuan-huan-cheng-zheng-shu-lcof/
 */
public class Str2Int {

    public int myAtoi(String str) {
        if (str == null) {
            return 0;
        }
        str = str.trim();
        if (str.length() == 0) {
            return 0;
        }
        char firstChar = str.charAt(0);
        if (!((firstChar >= '0' && firstChar <= '9')
                || firstChar == '+' || firstChar == '-')) {
            return 0;
        }
        int i = 0;
        boolean neg = firstChar == '-' ? true : false;
        if (firstChar == '+' || firstChar == '-') {
            i = 1;
        }
        long res = 0;
        // 遍历字符串，且当前遍历的字符是数字
        while (i < str.length() && str.charAt(i) >= '0'
                && str.charAt(i) <= '9') {
            res = res * 10 + str.charAt(i) - '0';
            // 负数
            if (neg && res - 1 > Integer.MAX_VALUE) {
                res = 1L + Integer.MAX_VALUE;
                break;
            }
            // 正数
            if (!neg && res > Integer.MAX_VALUE) {
                res = Integer.MAX_VALUE;
                break;
            }
            i++;
        }

        return neg ? (int) -res : (int) res;
    }

}
