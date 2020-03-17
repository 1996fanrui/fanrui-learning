package com.dream.str;

/**
 * @author fanrui
 * @time 2020-03-17 19:37:12
 * 字符串压缩
 * LeetCode :https://leetcode-cn.com/problems/compress-string-lcci/
 */
public class CompressString {

    public String compressString(String S) {
        if (S == null || S.length() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < S.length()) {
            char curChar = S.charAt(i++);
            int count = 1;
            while (i < S.length() && curChar == S.charAt(i)) {
                i++;
                count++;
            }
            stringBuilder.append(curChar).append(count);
        }

        String com = stringBuilder.toString();
        return com.length() < S.length() ? com : S;
    }

}
