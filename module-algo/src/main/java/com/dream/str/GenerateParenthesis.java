package com.dream.str;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanrui
 * @time 2020-03-14 01:26:27
 * 22. 括号生成
 * LeetCode 22 ：https://leetcode-cn.com/problems/generate-parentheses/
 * 给出 n 代表生成括号的对数，计算能够生成所有可能的并且有效的括号组合。
 */
public class GenerateParenthesis {


    public List<String> generateParenthesis(int n) {
        ArrayList<String> res = new ArrayList<>();
        recGen(res, 0, 0, n, new char[2*n]);
        return res;
    }


    private void recGen(List<String> list, int left, int right, int n, char[] str) {
        if (left == n && right == n) {
            list.add(new String(str));
            return;
        }

        // 当 left 小于 n 时，可以添加一个 左括号
        if (left < n) {
            str[left+right] = '(';
            recGen(list, left + 1, right, n, str);
        }

        // 当 right < left 时，可以添加一个 右括号
        if (right < left) {
            str[left+right] = ')';
            recGen(list, left, right + 1, n, str);
        }
    }


}
