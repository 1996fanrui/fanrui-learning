package com.dream.tree.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanrui
 * @time 2020-03-21 01:18:45
 * 查找出二叉树中和为某一值的所有路径
 * LeetCode 113：https://leetcode-cn.com/problems/path-sum-ii/
 * 剑指 Offer 34： https://leetcode-cn.com/problems/er-cha-shu-zhong-he-wei-mou-yi-zhi-de-lu-jing-lcof/
 */
public class PathSum {

    private List<List<Integer>> res;
    private int targetSum;

    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        targetSum = sum;
        recPath(root, 0, new ArrayList<>());
        return res;
    }

    private void recPath(TreeNode curNode, int curSum, List<Integer> curPath) {
        curSum += curNode.val;
        // curNode 是叶子节点，且当前路径累加和是 targetSum
        if (curNode.left == null && curNode.right == null
                && curSum == targetSum) {
            // 将 路径添加到 res 中
            List<Integer> okPath = new ArrayList<>(curPath);
            okPath.add(curNode.val);
            res.add(okPath);
            return;
        }

        // 当前节点加入到路径中
        curPath.add(curNode.val);
        // 递归遍历左右子树
        if (curNode.left != null) {
            recPath(curNode.left, curSum, curPath);
        }
        if (curNode.right != null) {
            recPath(curNode.right, curSum, curPath);
        }
        // 将当前节点从 路径中删除
        curPath.remove(curPath.size() - 1);
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
