package com.dream.str;


import java.util.Arrays;
import java.util.HashMap;

/**
 * @author fanrui
 * @time 2020-03-28 09:37:34
 * 820. 单词的压缩编码：求单词列表按照公共后缀压缩后的最短长度
 * LeetCode 820：https://leetcode-cn.com/problems/short-encoding-of-words/
 */
public class MinimumLengthEncoding {

    // 思路一：前缀树、字典树，但是单词是按照逆序来排序
    public int minimumLengthEncoding1(String[] words) {
        if (words == null || words.length == 0) {
            return 0;
        }
        TrieNode1 root = new TrieNode1();

        // 存储所有 end 节点的 TrieNode1 对应 word 的长度
        HashMap<TrieNode1, Integer> map = new HashMap<>();
        // 将所有单词倒序加入到 Trie 树中，且 Trie 树的叶子节点，isEnd 为 true
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() == 0) {
                continue;
            }
            map.put(addWord2Trie(root, words[i]), words[i].length() + 1);
        }

        int res = 0;
        // 遍历所有 end 的 TrieNode1，将当前仍然是 end 的 node 对应的单词长度输出
        for (HashMap.Entry<TrieNode1, Integer> entry : map.entrySet()) {
            TrieNode1 curTrieNode = entry.getKey();
            if (curTrieNode.isEnd) {
                res += entry.getValue();
            }
        }
        return res;
    }


    // 把单词添加到 Trie 中，并返回当前分支的最后一个节点
    private TrieNode1 addWord2Trie(TrieNode1 root, String word) {
        TrieNode1 cur = root;
        for (int i = word.length() - 1; i >= 0; i--) {
            char curChar = word.charAt(i);
            // 当前路径为空
            if (cur.next[curChar - 'a'] == null) {
                // 最后一个节点
                if (i == 0) {
                    cur.next[curChar - 'a'] = new TrieNode1(true);
                } else {
                    // 不是最后一个节点，所以 false
                    cur.next[curChar - 'a'] = new TrieNode1();
                }
            } else {
                // 当前节点已经存在，且不是最后一个节点，需要将 isEnd 更新为 false
                if (i != 0) {
                    cur.next[curChar - 'a'].isEnd = false;
                }
            }
            cur = cur.next[curChar - 'a'];
        }
        return cur;
    }


    private class TrieNode1 {
        TrieNode1[] next;
        boolean isEnd;

        public TrieNode1() {
            next = new TrieNode1[26];
            isEnd = false;
        }

        public TrieNode1(boolean isEnd) {
            next = new TrieNode1[26];
            this.isEnd = isEnd;
        }

    }


    public int minimumLengthEncoding(String[] words) {
        int len = 0;
        Trie trie = new Trie();
        // 先对单词列表根据单词长度由长到短排序
        Arrays.sort(words, (s1, s2) -> s2.length() - s1.length());
        // 单词插入trie，返回该单词增加的编码长度
        for (String word : words) {
            len += trie.insert(word);
        }
        return len;
    }

    // 定义tire
    class Trie {

        TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public int insert(String word) {
            TrieNode cur = root;
            boolean isNew = false;
            // 倒着插入单词
            for (int i = word.length() - 1; i >= 0; i--) {
                int c = word.charAt(i) - 'a';
                if (cur.children[c] == null) {
                    isNew = true; // 是新单词
                    cur.children[c] = new TrieNode();
                }
                cur = cur.children[c];
            }
            // 如果是新单词的话编码长度增加新单词的长度+1，否则不变。
            return isNew ? word.length() + 1 : 0;
        }
    }

    class TrieNode {
        char val;
        TrieNode[] children = new TrieNode[26];

        public TrieNode() {
        }
    }


}
