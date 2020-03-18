package com.dream.tree.algo;

/**
 * @author fanrui
 * @time 2020-03-18 21:59:59
 * 105. 从前序与中序遍历序列构造二叉树
 * LeetCode 105：https://leetcode-cn.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/
 * 剑指 Offer 7：https://leetcode-cn.com/problems/zhong-jian-er-cha-shu-lcof/submissions/
 */
public class BuildTreeFromPreorderAndInorderTraversal {


    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if(preorder == null || preorder.length == 0){
            return null;
        }
        return recBuild(preorder, 0, preorder.length-1,
                inorder, 0, inorder.length-1);
    }


    private TreeNode recBuild(int[] preorder, int preStart, int preEnd,
                              int[] inorder, int inStart, int inEnd){
        // 前序遍历第一个节点就是当前的 根节点
        TreeNode curNode = new TreeNode(preorder[preStart]);
        if(preStart == preEnd){
            return curNode;
        }

        // indexOfInorder 保存当前元素在 inorder 中的位置
        int indexOfInorder = inStart;
        while(preorder[preStart] != inorder[indexOfInorder]){
            indexOfInorder++;
        }

        // 左子树的节点数量
        int leftChildrenCount = indexOfInorder - inStart;
        if(leftChildrenCount > 0){
            // 数量大于 0，则构建左子树
            curNode.left = recBuild(preorder, preStart+1, preStart+leftChildrenCount,
                    inorder, inStart, indexOfInorder-1);
        }

        int rightChildrenCount = inEnd - indexOfInorder;
        if(rightChildrenCount > 0){
            curNode.right = recBuild(preorder, preEnd-rightChildrenCount+1, preEnd,
                    inorder, indexOfInorder+1, inEnd);
        }

        return curNode;
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
