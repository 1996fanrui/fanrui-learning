package com.dream.tree.algo;

/**
 * @author fanrui
 * @time 2020-03-22 10:19:21
 * 面试题54. 二叉搜索树的第k大节点
 * 剑指 Offer 54：https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-di-kda-jie-dian-lcof/
 */
public class KthLargest {

    int count;
    int res;

    public int kthLargest(TreeNode root, int k) {
        count = k;
        res = 0;
        inorderReverse(root);
        return res;
    }

    // 遍历顺序为： 右 中 左
    private void inorderReverse(TreeNode root) {
        if (root == null) {
            return;
        }

        // count == 0 表示结果已经计算好了，不再进行遍历了
        if (count == 0) {
            return;
        }

        // 先遍历右子树，再遍历 左子树
        inorderReverse(root.right);
        count--;
        if (count == 0) {
            res = root.val;
        }
        inorderReverse(root.left);
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
