package com.dream.tree.algo;

/**
 * 有一棵 完全二叉树 的根节点 root ，求出该树最后一层的最后一个节点。
 * 核心思路：遍历右子树的最左节点，如果是整个树的最后一层，要找的结果在右子树。如果不是最后一层，则结果在左子树。
 */
public class CompleteTreeFindLastNode {

    public static class TreeNode {
        public int value;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int data) {
            this.value = data;
        }
    }

    public static TreeNode findLastNode(TreeNode root) {
        return recursion(root, 1, mostLeftLevel(root, 1));
    }

    public static TreeNode recursion(TreeNode cur, int curLevel, int totalHeight) {
        // 已经到达了最后一层，已经是结果了。
        if (curLevel == totalHeight) {
            return cur;
        }
        // 当前不是最后一层，一定要往下面继续遍历。
        // 如果右子树为空，或者右子树的高度不是整个树的高度，则递归遍历左子树。否则递归遍历右子树。
        if (cur.right == null || mostLeftLevel(cur.right, curLevel + 1) < totalHeight) {
            return recursion(cur.left, curLevel + 1, totalHeight);
        } else {
            return recursion(cur.right, curLevel + 1, totalHeight);
        }
    }

    public static int mostLeftLevel(TreeNode cur, int curLevel) {
        while (cur.left != null) {
            cur = cur.left;
            curLevel++;
        }
        return curLevel;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        System.out.println(findLastNode(root).value);

        root.left = new TreeNode(2);
        System.out.println(findLastNode(root).value);

        root.right = new TreeNode(3);
        System.out.println(findLastNode(root).value);

        root.left.left = new TreeNode(4);
        System.out.println(findLastNode(root).value);

        root.left.right = new TreeNode(5);
        System.out.println(findLastNode(root).value);
        root.right.left = new TreeNode(6);
        System.out.println(findLastNode(root).value);

    }
}
