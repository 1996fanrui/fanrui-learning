package com.dream.str;

/**
 * @author fanrui
 * 比较版本号
 * LeetCode 165：https://leetcode-cn.com/problems/compare-version-numbers/
 */
public class CompareVersion {

    // 思路一： 所有字符串转换成 int，进行比较
    public int compareVersion1(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        for (int i = 0; i < v1.length || i < v2.length; i++) {
            String str1 = i < v1.length ? v1[i] : "0";
            String str2 = i < v2.length ? v2[i] : "0";
            int res = compare(str1, str2);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

    private int compare(String str1, String str2) {
        int num1 = Integer.parseInt(str1);
        int num2 = Integer.parseInt(str2);
        return num1 == num2 ? 0 : num1 > num2 ? 1 : -1;
    }


    // 思路二：字符串去掉前缀0，直接比较字符串长度，字符串长度相等的，再去比较字符
    public int compareVersion(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        for (int i = 0; i < v1.length || i < v2.length; i++) {
            String str1 = i < v1.length ? v1[i] : "0";
            String str2 = i < v2.length ? v2[i] : "0";
            int res = compareStr(str1, str2);
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }

    // 比较字符串
    private int compareStr(String str1, String str2) {
        // 移除前缀0以后，比较字符串长度即可，长度相同再依次比较字符
        String s1 = removePrefixZero(str1);
        String s2 = removePrefixZero(str2);
        if (s1.length() < s2.length()) {
            return -1;
        } else if (s1.length() > s2.length()) {
            return 1;
        } else {
            for (int i = 0; i < s1.length(); i++) {
                if (s1.charAt(i) == s2.charAt(i)) {
                    continue;
                }
                if (s1.charAt(i) < s2.charAt(i)) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return 0;
        }
    }

    // 去掉字符串中的前缀 0
    private String removePrefixZero(String str) {
        int start = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0') {
                break;
            }
            start++;
        }
        return str.substring(start);
    }

}
