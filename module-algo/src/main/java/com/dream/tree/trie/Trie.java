package com.dream.tree.trie;

/**
 * @author fanrui
 * @time 2020-03-15 01:46:48
 * 208. 实现 Trie (前缀树)
 * LeetCode 208： https://leetcode-cn.com/problems/implement-trie-prefix-tree/
 */
public class Trie {

    private TrieNode root;

    private class TrieNode {
        char val;
        boolean isEnd;
        TrieNode[] children = new TrieNode[26];

        public TrieNode(char val) {
            this.val = val;
            this.isEnd = false;
        }
    }

    /**
     * Initialize your data structure here.
     */
    public Trie() {
        root = new TrieNode('0');
    }

    /**
     * Inserts a word into the trie.
     */
    public void insert(String word) {
        char[] chars = word.toCharArray();
        TrieNode cur = root;
        for (int i = 0; i < chars.length; i++) {
            if (cur.children[chars[i] - 'a'] == null) {
                cur.children[chars[i] - 'a'] = new TrieNode(chars[i]);
            }
            cur = cur.children[chars[i] - 'a'];
        }
        cur.isEnd = true;
    }

    /**
     * Returns if the word is in the trie.
     */
    public boolean search(String word) {
        char[] chars = word.toCharArray();
        TrieNode cur = root;
        for (int i = 0; i < chars.length; i++) {
            if (cur.children[chars[i] - 'a'] == null) {
                return false;
            }
            cur = cur.children[chars[i] - 'a'];
        }
        return cur.isEnd;
    }

    /**
     * Returns if there is any word in the trie that starts with the given prefix.
     */
    public boolean startsWith(String prefix) {

        char[] chars = prefix.toCharArray();
        TrieNode cur = root;
        for (int i = 0; i < chars.length; i++) {
            if (cur.children[chars[i] - 'a'] == null) {
                return false;
            }
            cur = cur.children[chars[i] - 'a'];
        }
        return true;
    }
}
