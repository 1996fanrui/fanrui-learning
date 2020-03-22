package com.dream.tree.algo.recursion;


/**
 * @author fanrui
 * LeetCode 104：https://leetcode-cn.com/problems/maximum-depth-of-binary-tree/submissions/
 * 剑指 Offer 55-I： https://leetcode-cn.com/problems/er-cha-shu-de-shen-du-lcof/
 * 求树的高度 或者 最大深度
 * 递归遍历，左右子树，拿到左树高度，拿到右树高度。最大值 +1 即可
 */
public class TreeHeight {

    public static class TreeNode {
        public int value;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int data) {
            this.value = data;
        }
    }

    // 思路一：递归遍历左子树和右子树，左右子树的最高高度 +1
    public static int getTreeHeight(TreeNode root) {
        if (root == null) {
            return 0;
        }
        // 拿到左树高度，拿到右树高度。最大值 +1
        int leftHeight = getTreeHeight(root.left);
        int rightHeight = getTreeHeight(root.right);
        return Math.max(leftHeight, rightHeight) + 1;
    }


    int res = 0;

    // 思路二：递归时，将 level 传递下去，遍历到 叶节点更新 MaxHeight
    public int maxDepth(TreeNode root) {
        res = 0;
        if (root == null) {
            return res;
        }
        preOrder(root, 1);
        return res;
    }

    private void preOrder(TreeNode root, int level) {
        if (root == null) {
            return;
        }
        res = Math.max(res, level);
        preOrder(root.left, level + 1);
        preOrder(root.right, level + 1);
    }


}
