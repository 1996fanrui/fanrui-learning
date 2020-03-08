package com.dream.dp;

/**
 * @author fanrui
 * @time 2020-03-08 13:53:25
 * 最长回文子串
 * LeetCode 5：https://leetcode-cn.com/problems/longest-palindromic-substring/
 *
 * 最优解是 Manacher 算法，这里使用 dp 解题
 */
public class LongestPalindromeSubString {


    public String longestPalindrome(String s) {
        if(s==null || "".equals(s)){
            return "";
        }
        char[] charArray = s.toCharArray();
        boolean[][] dp = new boolean[charArray.length][charArray.length];
        int maxLen= 0;
        int start = -1;
        // len 从 1 开始，最长为 charArray 的 length
        for (int len = 1; len <= charArray.length; len++) {
            for (int i = 0; i < charArray.length; i++) {

                // i 为当前串的开始位置， j 为结束位置
                int j = i + len - 1;
                if(j >= charArray.length){
                    break;
                }
                if((len == 1 || len == 2 || dp[i+1][j-1]) &&
                        charArray[i] == charArray[j]){
                    dp[i][j] = true;
                    if(len> maxLen){
                        maxLen = len;
                        start = i;
                    }
                }
            }
        }
        return String.valueOf(charArray,start, maxLen);
    }

}
