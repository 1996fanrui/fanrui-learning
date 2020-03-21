package com.dream.tree.algo;

/**
 * @author fanrui
 * @time 2020-03-21 10:51:38
 * 二叉搜索树与双向链表（BST 转换成有序的双向链表）
 * LeetCode 426（会员题目）: https://leetcode-cn.com/problems/convert-binary-search-tree-to-sorted-doubly-linked-list/
 * 剑指 Offer 36：https://leetcode-cn.com/problems/er-cha-sou-suo-shu-yu-shuang-xiang-lian-biao-lcof/
 */
public class BST2LinkedList {

    private Node res;
    private Node cur;

    public Node treeToDoublyList(Node root) {
        if(root == null){
            return null;
        }

        // 搞一个 哨兵，便于编码
        res = new Node();
        cur = res;
        recInorder(root);

        // 将链表变成循环链表
        cur.right = res.right;
        res = res.right;
        res.left = cur;
        return res;
    }


    // 中序遍历
    private void recInorder(Node root) {
        if (root == null) {
            return;
        }

        // 遍历左子树
        recInorder(root.left);

        // 将当前 root 节点串在 res 链表中
        cur.right = root;
        root.left = cur;
        cur = cur.right;

        // 遍历右子树
        recInorder(root.right);
    }


    public class Node {
        public int val;
        public Node left;
        public Node right;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right) {
            val = _val;
            left = _left;
            right = _right;
        }
    }

    ;


}
