package com.dream.tree.algo;

import java.util.LinkedList;

/**
 * @author fanrui
 * @time 2020-03-13 21:49:42
 * 111. 二叉树的最小深度
 * LeetCode 111: https://leetcode-cn.com/problems/minimum-depth-of-binary-tree/
 */
public class MinDepth {

    // 思路一：按层次遍历
    public static class Pair {
        TreeNode node;
        int level;

        public Pair(TreeNode node, int level) {
            this.node = node;
            this.level = level;
        }
    }

    public int minDepth2(TreeNode root) {
        if (root == null) {
            return 0;
        }
        LinkedList<Pair> queue = new LinkedList<>();
        queue.add(new Pair(root, 1));

        while (!queue.isEmpty()) {
            Pair curPair = queue.pollFirst();
            TreeNode curNode = curPair.node;
            if (curNode.left == null && curNode.right == null) {
                return curPair.level;
            }
            addPair2Queue(queue, curNode.left, curPair.level + 1);
            addPair2Queue(queue, curNode.right, curPair.level + 1);
        }
        return 0;
    }


    private void addPair2Queue(LinkedList<Pair> queue, TreeNode node, int level) {
        if (node == null) {
            return;
        }
        queue.add(new Pair(node, level));
    }


    // 思路二：递归方案
    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int leftMinDepth = minDepth(root.left);
        int rightMinDepth = minDepth(root.right);

        if (leftMinDepth != 0 && rightMinDepth != 0) {
            return Math.min(leftMinDepth, rightMinDepth) + 1;
        } else {
            return Math.max(leftMinDepth, rightMinDepth) + 1;
        }

    }

    public static class TreeNode {
        public int value;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int data) {
            this.value = data;
        }
    }

}
