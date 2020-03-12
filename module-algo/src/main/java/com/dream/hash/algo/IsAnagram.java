package com.dream.hash.algo;

import java.util.HashMap;

/**
 * @author fanrui
 * @time 2020-03-12 23:38:35
 * 有效的字母异位词
 * LeetCode 242： https://leetcode-cn.com/problems/valid-anagram/
 */
public class IsAnagram {

    //  方法一：借助 HashMap
    public boolean isAnagram(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return false;
        }

        HashMap<Character, Integer> sMap = generateMap(s);
        HashMap<Character, Integer> tMap = generateMap(t);

        if (sMap.size() != tMap.size()) {
            return false;
        }

        for (HashMap.Entry<Character, Integer> entry : sMap.entrySet()) {
            Integer count = tMap.get(entry.getKey());
            if (count == null || !count.equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }


    private HashMap<Character, Integer> generateMap(String str) {

        HashMap<Character, Integer> map = new HashMap<>(
                str.length() + (str.length() >> 1));
        char[] sChars = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            int count = map.getOrDefault(sChars[i], 0);
            map.put(sChars[i], ++count);
        }
        return map;
    }



    //  方法二：构建一个长度为 26 的数组
    public boolean isAnagram2(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return false;
        }

        int[] sArray = generateArray(s);
        int[] tArray = generateArray(t);

        for (int i = 0; i < sArray.length; i++) {
            if(sArray[i] != tArray[i]){
                return false;
            }
        }

        return true;
    }


    private int[] generateArray(String str) {
        int[] res = new int[26];
        char[] sChars = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            res[sChars[i]-'a']++;
        }
        return res;
    }

}
