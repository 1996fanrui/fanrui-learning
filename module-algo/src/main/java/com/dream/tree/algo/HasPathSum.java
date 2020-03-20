package com.dream.tree.algo;

/**
 * @author fanrui
 * @time 2020-03-21 01:44:28
 * 判断二叉树中是否存在和为某一值的路径
 * LeetCode 112： https://leetcode-cn.com/problems/path-sum/
 */
public class HasPathSum {

    private int targetSum;
    private boolean res;

    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null) {
            return false;
        }
        targetSum = sum;
        res = false;
        recPath(root, 0);
        return res;
    }

    private void recPath(TreeNode curNode, int curSum) {
        // 结果已经为 true，直接返回
        if(res){
            return;
        }
        curSum += curNode.val;

        // curNode 是叶子节点，且当前路径累加和是 targetSum
        if (curNode.left == null && curNode.right == null
                && curSum == targetSum) {
            res = true;
            return;
        }

        // 递归遍历左右子树
        if (curNode.left != null) {
            recPath(curNode.left, curSum);
        }
        if (curNode.right != null) {
            recPath(curNode.right, curSum);
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
