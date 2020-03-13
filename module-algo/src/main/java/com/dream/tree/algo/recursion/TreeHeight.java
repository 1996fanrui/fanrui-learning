package com.dream.tree.algo.recursion;


/**
 * @author fanrui
 * LeetCode 104：https://leetcode-cn.com/problems/maximum-depth-of-binary-tree/submissions/
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

    public static int getTreeHeight(TreeNode root){
        if(root == null){
            return 0;
        }
        // 拿到左树高度，拿到右树高度。最大值 +1
        int leftHeight = getTreeHeight(root.left);
        int rightHeight = getTreeHeight(root.right);
        return Math.max(leftHeight, rightHeight) + 1;
    }




}
