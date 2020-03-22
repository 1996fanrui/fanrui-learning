package com.dream.tree.algo.recursion;

/**
 * @author fanrui
 * @time  2020-03-22 10:57:47
 * 判断一棵树，是否是平衡树
 * LeetCode 110： https://leetcode-cn.com/problems/balanced-binary-tree/submissions/
 * 剑指 Offer 55-II： https://leetcode-cn.com/problems/ping-heng-er-cha-shu-lcof/submissions/
 */
public class IsBalancedTree {

    public boolean isBalanced(TreeNode root) {
        return recCheckBalance(root).isBalanced;
    }

    private ReturnType recCheckBalance(TreeNode root) {
        if (root == null) {
            return ReturnType.NULL_RETURN;
        }

        ReturnType leftRes = recCheckBalance(root.left);
        // 左子树不平衡，直接返回
        if (!leftRes.isBalanced) {
            return leftRes;
        }

        ReturnType rightRes = recCheckBalance(root.right);
        // 右子树不平衡，直接返回
        if (!rightRes.isBalanced) {
            return rightRes;
        }

        int curHeight = Math.max(leftRes.height, rightRes.height) + 1;
        return new ReturnType(curHeight,
                Math.abs(leftRes.height - rightRes.height) <= 1);
    }

    public static class ReturnType {
        public static ReturnType NULL_RETURN = new ReturnType(0, true);
        int height;
        boolean isBalanced;

        public ReturnType(int height, boolean isBalanced) {
            this.height = height;
            this.isBalanced = isBalanced;
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
