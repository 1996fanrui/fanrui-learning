package com.dream.str;

/**
 * @author fanrui
 * KMP：两个字符串 str1 和 str2，判断 str2 是不是  str1 的一个子串？如果是，返回 在 str1 中开始的位置。
 * LeetCode 28:https://leetcode-cn.com/problems/implement-strstr/
 */
public class KMP {

    public int strStr(String s, String m) {
        if (s == null || m == null || s.length() < m.length()) {
            return -1;
        }
        if (m.length() == 0) {
            return 0;
        }
        char[] str1 = s.toCharArray();
        char[] str2 = m.toCharArray();
        int index1 = 0;
        int index2 = 0;
        // 求出 next 数组
        int[] next = getNextArray(str2);
        // 有一个字符串匹配到尾部，则跳出循环
        while (index1 < str1.length && index2 < str2.length) {
            // 两个字符串当前 index 位置匹配成功，则 两个 index 右移
            if (str1[index1] == str2[index2]) {
                index1++;
                index2++;
            } else if (index2 == 0) {
                // 两个字符串当前 index 位置不能匹配成功，
                // 且 str2 目前从头开始匹配了，则 只能 index1 右移
                index1++;
            } else {
                // 两个字符串当前 index 位置不能匹配成功，
                // 且 str2 当前不是在匹配第 0 个元素，则 index2 往前跳
                index2 = next[index2];
            }
        }
        return index2 == str2.length ? index1 - index2 : -1;
    }

    public int[] getNextArray(char[] str2) {
        if (str2.length == 1) {
            return new int[]{-1};
        }
        int[] next = new int[str2.length];
        next[0] = -1;
        next[1] = 0;
        // pos 为当前 next 数组要计算的元素对应的 index
        int pos = 2;
        // cn 为当前求得的前缀长度
        int cn = 0;
        while (pos < next.length) {
            // 当前 pos 的前一个位置与 cn 位置元素相同，
            // 则 pos 对应的结果为 cn+1
            // pos 要向右移动一位，cn 值也要 +1
            if (str2[pos - 1] == str2[cn]) {
                next[pos++] = ++cn;
            } else if (cn == 0) {
                // 当前 pos 的前一个位置与 cn 位置元素不相同，
                // 且 cn 已经跳到 0，则 next[pos] = 0，
                // 且 pos 要向右移动一位
                next[pos++] = 0;
            } else {
                // 当前 pos 的前一个位置与 cn 位置元素不相同，
                // 且 cn 还没跳到 0，则 cn 跳到 next[cn] 的位置
                cn = next[cn];
            }
        }
        return next;
    }


}
