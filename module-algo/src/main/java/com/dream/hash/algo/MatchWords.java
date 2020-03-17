package com.dream.hash.algo;

/**
 * @author fanrui
 * 拼写单词
 * LeetCode 1160：https://leetcode-cn.com/problems/find-words-that-can-be-formed-by-characters/
 */
public class MatchWords {

    public int countCharacters(String[] words, String chars) {

        if (words == null || words.length == 0 || chars == null || chars.length() == 0) {
            return 0;
        }

        int[] table = new int[26];
        for (int i = 0; i < chars.length(); i++) {
            table[chars.charAt(i) - 'a']++;
        }

        int res = 0;
        for (int i = 0; i < words.length; i++) {
            if (canSpellWords(words[i], table)) {
                res += words[i].length();
            }
        }
        return res;
    }


    private boolean canSpellWords(String word, int[] table) {
        int[] wordCount = new int[26];
        for (int i = 0; i < word.length(); i++) {
            wordCount[word.charAt(i) - 'a']++;
            if (wordCount[word.charAt(i) - 'a'] > table[word.charAt(i) - 'a']) {
                return false;
            }
        }
        return true;
    }

}
