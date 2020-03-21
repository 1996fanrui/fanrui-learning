package com.dream.hash.algo;


/**
 * @author fanrui
 * @time 2020-03-21 23:45:48
 * 第一个只出现一次的字符
 * 剑指 Offer 50： https://leetcode-cn.com/problems/di-yi-ge-zhi-chu-xian-yi-ci-de-zi-fu-lcof/
 */
public class FirstUniqChar {

    public char firstUniqChar(String s) {

        // 存放每个字符出现的次数
        int[] map = new int[256];

        char[] chars = s.toCharArray();

        // 将 s 中字符的个数存储在 map 中
        for (int i = 0; i < chars.length; i++) {
            map[chars[i]]++;
        }
        // 从头往后遍历，找出现一次的元素
        for (int i = 0; i < chars.length; i++) {
            if(map[chars[i]] == 1){
                return chars[i];
            }
        }
        return ' ';
    }
}
