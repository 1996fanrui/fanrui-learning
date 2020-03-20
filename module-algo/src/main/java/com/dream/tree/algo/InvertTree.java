package com.dream.tree.algo;

/**
 * @author fanrui
 * @time 2020-03-20 12:39:11
 * 翻转二叉树（二叉树的镜像）
 * 剑指 27：https://leetcode-cn.com/problems/er-cha-shu-de-jing-xiang-lcof/
 * LeetCode 226：https://leetcode-cn.com/problems/invert-binary-tree/
 */
public class InvertTree {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        // 交换左右孩子
        TreeNode tmp = root.left;
        root.left = root.right;
        root.right = tmp;

        // 递归遍历左右子树
        invertTree(root.left);
        invertTree(root.right);
        return root;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }
}
