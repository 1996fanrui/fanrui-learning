package com.dream.str;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author fanrui
 * @time 2020-04-04 23:08:48
 * 151. 翻转字符串里的单词： 给定一个字符串，逐个翻转字符串中的每个单词。
 * LeetCode 151: https://leetcode-cn.com/problems/reverse-words-in-a-string/
 * 剑指 58-I :https://leetcode-cn.com/problems/fan-zhuan-dan-ci-shun-xu-lcof/
 */
public class ReverseWords {

    public String reverseWords(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        // 去重头尾部的空格，按照空格切分，然后 reverse，最后再用 空格进行拼接
        List<String> list = Arrays.asList(s.trim().split("\\s+"));
        Collections.reverse(list);
        return String.join(" ", list);
    }

}
