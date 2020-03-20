package com.dream.tree.algo;

/**
 * @author fanrui
 * 判断 B 是不是 A 的子结构
 * 剑指 26：https://leetcode-cn.com/problems/shu-de-zi-jie-gou-lcof/
 */
public class IsSubStructure {

    public boolean isSubStructure(TreeNode A, TreeNode B) {
        boolean res = false;
        if (A == null || B == null) {
            return false;
        }
        // A 的当前节点 能与 B 的根节点匹配成功，则匹配整个 B 的子树
        if (A.val == B.val) {
            res = checkIsMatch(A, B);
        }
        // 未匹配成功，则递归匹配左右子树
        if (!res) {
            res = isSubStructure(A.left, B);
        }
        if (!res) {
            res = isSubStructure(A.right, B);
        }
        return res;
    }

    // 检查 A 树能否与 B 匹配
    private boolean checkIsMatch(TreeNode A, TreeNode B) {
        // B 为空，可以匹配。
        if (B == null) {
            return true;
        } else if (A == null) {
            // B 不为空，A 为空，不能匹配
            return false;
        } else if (A.val == B.val) {
            // A 和 B 能匹配，还要继续匹配左右子树
            return checkIsMatch(A.left, B.left) && checkIsMatch(A.right, B.right);
        } else {
            // A 和 B 本身值不同，则不匹配
            return false;
        }
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
