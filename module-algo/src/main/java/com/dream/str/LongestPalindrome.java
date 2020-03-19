package com.dream.str;

/**
 * @author fanrui
 * @time 2020-03-19 00:49:19
 * 给定字母能组合成的最长的回文串
 * LeetCode 409：https://leetcode-cn.com/problems/longest-palindrome/
 */
public class LongestPalindrome {

    public int longestPalindrome(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        int[] count = new int[128];
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i)]++;
        }
        int res = 0;
        boolean isAdd = false;
        for (int i = 'A'; i <= 'z'; i++) {
            res += count[i] / 2 * 2;
            if (!isAdd && (count[i] & 1) == 1) {
                isAdd = true;
            }
        }
        if (isAdd) {
            res++;
        }
        return res;
    }

}
