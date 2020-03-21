package com.dream.str;

import java.util.HashSet;

/**
 * @author fanrui
 * @time 2020-03-21 13:50:26
 * 字符串的排列
 * LeetCode 38：https://leetcode-cn.com/problems/zi-fu-chuan-de-pai-lie-lcof/
 */
public class StringPermutation {


    // 思路一： dfs
    // 将 字符串转换为 char 数组。用一个 boolean 类型的数组做标记，标记每一位是否被使用。
    // dfs 遍历即可。再用一个 char 数组表示当前路径的顺序。只要遍历到最后一位即生成 String。
    HashSet<String> res;

    public String[] permutation1(String s) {
        if (s == null || s.length() == 0) {
            return new String[0];
        }
        char[] ch = s.toCharArray();
        boolean[] isUsed = new boolean[s.length()];
        char[] path = new char[s.length()];
        res = new HashSet<>();
        recGenString(0, ch, path, isUsed);
        return res.toArray(new String[0]);
    }

    private void recGenString(int index, char[] ch, char[] path, boolean[] isUsed) {
        // 遍历到尾部，生成字符串
        if (index == ch.length) {
            res.add(new String(path));
            return;
        }
        // 把当前位置所有可选的字符遍历一遍
        for (int i = 0; i < ch.length; i++) {
            // i 位置字符已经被使用了，则 continue
            if (isUsed[i]) {
                continue;
            }
            // i 位置字符还没有被使用，标志为使用，填写当前字符的路径
            isUsed[i] = true;
            path[index] = ch[i];
            recGenString(index + 1, ch, path, isUsed);
            isUsed[i] = false;
        }
    }


    // 思路二：交换字符的思想，将所有字符交换到第一个，然后后续的所有字符再交换到第二个。。。依次类推
    public String[] permutation(String s) {
        if (s == null || s.length() == 0) {
            return new String[0];
        }
        char[] ch = s.toCharArray();
        res = new HashSet<>();
        recSwap(0, ch);
        return res.toArray(new String[0]);
    }

    // 递归遍历，认为 index 之前的数据已经确定位置了，
    // 然后后续各种情况进行罗列，每层递归确定一个字符。
    private void recSwap(int index, char[] ch) {
        // 遍历到尾部，生成字符串
        if (index == ch.length) {
            res.add(new String(ch));
            return;
        }

        // 将 index 后续的所有字符尝试交换到 index 位置
        for (int i = index; i < ch.length; i++) {
            // 将 i 位置的数据交换到 index，表示 index 位置确定了，然后 index 之后的数据进行递归遍历
            boolean flag = swap(ch, i, index);
            // 如果 i 和 j 不同，且 两个位置字符相同，则进行剪枝，没必要递归了
            if(!flag){
                continue;
            }
            // index 及 index 之前的字符已经全部确定了，递归后面的字符
            recSwap(index + 1, ch);

            // 为了保证正确性，处理完后，需要把要交换的数据换回来
            swap(ch, index, i);
        }
    }

    // 在 swap 的基础上增加了，如果 i 和 j 不同，且 两个位置字符相同，则返回 false
    private boolean swap(char[] ch, int i, int j) {
        if (i == j) {
            return true;
        }
        if(ch[i] == ch[j]){
            return false;
        }
        char tmp = ch[i];
        ch[i] = ch[j];
        ch[j] = tmp;
        return true;
    }


}
