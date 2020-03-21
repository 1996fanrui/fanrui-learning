package com.dream.dp;

/**
 * @author fanrui
 * @time 2020-03-21 22:05:48
 * 把数字翻译成字符串
 * 剑指 Offer 46： https://leetcode-cn.com/problems/ba-shu-zi-fan-yi-cheng-zi-fu-chuan-lcof/
 */
public class TranslateNum {

    public int translateNum(int num) {
        // num 只有一位
        if (num < 9) {
            return 1;
        }

        String str = String.valueOf(num);
        char[] chars = str.toCharArray();

        int dpBefore2 = 1;
        int dpBefore1 = 1;
        int res = 0;
        for (int i = 1; i < chars.length; i++) {
            // 最近两个字符，组合关系在 0~25 之间
            if (chars[i - 1] <= '1' || (chars[i - 1] == '2' && chars[i] <= '5')) {
                //  dp[i] = dp[i-1] + dp[i-2]
                res = dpBefore1 + dpBefore2;
            } else {
                //  dp[i] = dp[i-1]
                res = dpBefore1;
            }
            dpBefore2 = dpBefore1;
            dpBefore1 = res;
        }
        return res;
    }

}
