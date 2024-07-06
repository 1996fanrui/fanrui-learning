package com.dream.tree.algo.recursion;

/**
 * 124. 二叉树中的最大路径和
 * https://leetcode.cn/problems/binary-tree-maximum-path-sum/
 */
public class MaxPathSum {

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public class ResultType {
        int totalSum;
        int singleSum;

        public ResultType(int totalSum, int singleSum) {
            this.totalSum = totalSum;
            this.singleSum = singleSum;
        }
    }

    public int maxPathSum(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return recursion(root).totalSum;
    }


    public ResultType recursion(TreeNode cur) {
        if (cur == null) {
            return new ResultType(Integer.MIN_VALUE, 0);
        }
        final ResultType leftResult = recursion(cur.left);
        final ResultType rightResult = recursion(cur.right);

        // 当前 node 做为路径时，最大路径是当前节点 + 左孩子的最大 单路径 + 右孩子的最大 单路径
        // 如果左右孩子的路径为 负数，可以选择放弃该路径，所以跟 0 取 max
        int totalSum = cur.val + Math.max(leftResult.singleSum, 0) + Math.max(rightResult.singleSum, 0);

        // 当前 node 做为路径时，最大单路径是 当前节点 + （左孩子的最大 单路径  或 右孩子的最大 单路径）
        // 如果左右孩子的单路径为 负数，可以选择放弃，所以跟 0 取 max
        int singleSum = cur.val + Math.max(Math.max(leftResult.singleSum, rightResult.singleSum), 0);

        // 最大路径可能来自于 子孩子，所以向上层返回。
        return new ResultType(Math.max(totalSum, Math.max(leftResult.totalSum, rightResult.totalSum)), singleSum);
    }
}
