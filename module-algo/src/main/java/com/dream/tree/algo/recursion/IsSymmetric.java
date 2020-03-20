package com.dream.tree.algo.recursion;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author fanrui
 * @time 2020-03-20 12:57:08
 * 判断一棵树是否是对称二叉树
 * LeetCode 101：https://leetcode-cn.com/problems/symmetric-tree/
 * 剑指 28：https://leetcode-cn.com/problems/dui-cheng-de-er-cha-shu-lcof/
 */
public class IsSymmetric {

    // 思路一：递归
    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return checkSymmetric(root.left, root.right);
    }

    private boolean checkSymmetric(TreeNode left, TreeNode right) {
        // 两个节点全为空则返回 true
        if (left == null && right == null) {
            return true;
        }
        // 只有一个节点为 null，则为 false
        if (left == null || right == null) {
            return false;
        }

        // 当前两个节点是否能够匹配，不匹配直接 false
        if (left.val != right.val) {
            return false;
        }
        // 检查左子树的左和 右子树的右。且检查 左子树的右和右子树的左
        return checkSymmetric(left.left, right.right)
                && checkSymmetric(left.right, right.left);
    }


    // 思路二：迭代
    public boolean isSymmetric2(TreeNode root) {
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        q.add(root);
        while (!q.isEmpty()) {
            TreeNode t1 = q.poll();
            TreeNode t2 = q.poll();
            if (t1 == null && t2 == null) {
                continue;
            }
            if (t1 == null || t2 == null) {
                return false;
            }
            if (t1.val != t2.val) {
                return false;
            }
            q.add(t1.left);
            q.add(t2.right);
            q.add(t1.right);
            q.add(t2.left);
        }
        return true;
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
