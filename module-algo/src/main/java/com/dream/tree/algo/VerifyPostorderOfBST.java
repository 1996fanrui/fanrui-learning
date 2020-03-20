package com.dream.tree.algo;

/**
 * @author fanrui
 * @time 2020-03-21 00:49:02
 * 二叉搜索树的后续遍历序列
 * 剑指 Offer 33：https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-hou-xu-bian-li-xu-lie-lcof/
 */
public class VerifyPostorderOfBST {

    public boolean verifyPostorder(int[] postorder) {
        if (postorder == null && postorder.length == 0) {
            return false;
        }
        return verifyPostorderOfBST(postorder, 0, postorder.length - 1);
    }


    private boolean verifyPostorderOfBST(int[] postorder, int start, int end) {
        // 如果 序列小于等于 1，都返回 true
        if (start >= end) {
            return true;
        }
        int root = postorder[end];
        // 从左到右遍历，找第一个大于 root 的元素 index，放到 firstGreat
        int firstGreat = start;
        for (; firstGreat < end; firstGreat++) {
            if (postorder[firstGreat] > root) {
                break;
            }
        }

        // 从 firstGreat + 1 位置开始遍历，理论来讲，都是 大于 root 的数，
        // 如果找到了小于 root 的元素。直接返回 false
        for (int i= firstGreat + 1; i < end; i++) {
            if (postorder[i] < root) {
                return false;
            }
        }

        // 递归遍历左子树和右子树
        return verifyPostorderOfBST(postorder, start, firstGreat-1)
                && verifyPostorderOfBST(postorder, firstGreat, end-1);
    }

}
