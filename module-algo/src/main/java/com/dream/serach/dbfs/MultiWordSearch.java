package com.dream.serach.dbfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fanrui
 * 矩阵中多个单词搜索
 * LeetCode 212：https://leetcode-cn.com/problems/word-search-ii/
 */
public class MultiWordSearch {

    // 矩阵的总行数和总列数
    private int rowCount;
    private int colCount;

    private Set<String> set = new HashSet<>();
    private Trie trie;

    // 方案一：普通版，判断是否是前缀是从头开始的匹配
    public List<String> findWords1(char[][] board, String[] words) {
        if (board == null || board.length == 0 || board[0].length == 0
                || words == null || words.length == 0) {
            return new ArrayList<>();
        }
        rowCount = board.length;
        colCount = board[0].length;
        set.clear();
        boolean[][] visited = new boolean[rowCount][colCount];

        trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                dfs(board, "", i, j, visited);
            }
        }
        return new ArrayList<>(set);
    }

    // 普通版，判断是否是前缀是从头开始的匹配
    private void dfs(char[][] board, String path
            , int curRow, int curCol
            , boolean[][] visited) {

        // 当前位置越界，或当前位置已经被访问过，直接结束
        if (curRow >= rowCount || curRow < 0 ||
                curCol >= colCount || curCol < 0 ||
                visited[curRow][curCol]) {
            return;
        }
        String curPath = path + board[curRow][curCol];

        // 当前路径不属于 trie 的前缀，直接结束
        if (!trie.startsWith(curPath)) {
            return;
        }

        // 当前路径在 trie 中存在，则认为可搜索到，添加到结果 set 中
        if (trie.search(curPath)) {
            set.add(curPath);
        }

        // 将当前位置标记为已访问
        visited[curRow][curCol] = true;
        // 往四个方向去遍历
        dfs(board, curPath, curRow - 1, curCol, visited);
        dfs(board, curPath, curRow + 1, curCol, visited);
        dfs(board, curPath, curRow, curCol - 1, visited);
        dfs(board, curPath, curRow, curCol + 1, visited);
        // 所有路径都不同，则请求访问记录，并返回 false
        visited[curRow][curCol] = false;
    }


    // 方案二：优化前缀匹配效率，只匹配当前字符
    public List<String> findWords(char[][] board, String[] words) {
        if (board == null || board.length == 0 || board[0].length == 0
                || words == null || words.length == 0) {
            return new ArrayList<>();
        }
        rowCount = board.length;
        colCount = board[0].length;
        set.clear();
        boolean[][] visited = new boolean[rowCount][colCount];

        trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                dfs(board, trie.root, i, j, visited);
            }
        }
        return new ArrayList<>(set);
    }


    // 普通版，判断是否是前缀是从头开始的匹配
    private void dfs(char[][] board, Trie.TrieNode trieNode
            , int curRow, int curCol
            , boolean[][] visited) {

        // 当前位置越界，或当前位置已经被访问过，直接结束
        if (curRow >= rowCount || curRow < 0 ||
                curCol >= colCount || curCol < 0 ||
                visited[curRow][curCol]) {
            return;
        }

        Trie.TrieNode[] children = trieNode.children;
        // 当前的 trieNode 中不包含当前字符，则表示路径不存在直接结束
        if (children[board[curRow][curCol] - 'a'] == null) {
            return;
        }

        Trie.TrieNode node = children[board[curRow][curCol] - 'a'];
        // 表示当前是一个单词的结束
        if(node.isEnd){
            set.add(node.word);
        }

        // 将当前位置标记为已访问
        visited[curRow][curCol] = true;
        // 往四个方向去遍历
        dfs(board, node, curRow - 1, curCol, visited);
        dfs(board, node, curRow + 1, curCol, visited);
        dfs(board, node, curRow, curCol - 1, visited);
        dfs(board, node, curRow, curCol + 1, visited);
        // 所有路径都不同，则请求访问记录，并返回 false
        visited[curRow][curCol] = false;
    }

}

class Trie {

    TrieNode root;

    class TrieNode {
        char val;
        boolean isEnd;
        String word;
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
        cur.word = word;
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
