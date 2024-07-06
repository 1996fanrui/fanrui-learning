package com.dream.tree.traversal;

import java.util.LinkedList;
import java.util.List;

/**
 * 199. 二叉树的右视图
 * https://leetcode.cn/problems/binary-tree-right-side-view/description/
 */
public class RightSideView {

    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new LinkedList<>();
        recursion(result, root, 0);
        return result;
    }

    private void recursion(List<Integer> result, TreeNode cur, int curLevel) {
        if (cur == null) {
            return;
        }
        if (curLevel == result.size()) {
            result.add(cur.val);
        }
        recursion(result, cur.right, curLevel + 1);
        recursion(result, cur.left, curLevel + 1);
    }

    public static class TreeNode {
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
}
