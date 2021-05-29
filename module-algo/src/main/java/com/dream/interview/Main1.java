package com.dream.interview;


/**
 * 找完全二叉树最后一层的 最后一个节点
 */
public class Main1 {


    public static void main(String[] args) {

        TreeNode root = new TreeNode(9);
        TreeNode left = new TreeNode(8);
        left.left = new TreeNode(6);

        root.left = left;
        root.right = new TreeNode(7);
        rec(root);
    }

    private static void rec(TreeNode cur) {
        if(cur == null) {
            return;
        }

        int leftHeight  = getHeight(cur.left);
        if(leftHeight == 0) {
            // 找到了
            System.out.println(cur.val);
            return;
        }

        int rightHeight  = getHeight(cur.right);

        if (leftHeight == rightHeight) {
            rec(cur.right);
        } else {
            rec(cur.left);
        }
    }

    private static int getHeight(TreeNode cur) {
        if (cur == null) {
            return 0;
        }
        int height = 1;
        while (cur.left != null) {
            height++;
            cur = cur.left;
        }
        return height;
    }


    private static class TreeNode {

        TreeNode left;
        TreeNode right;
        int val;

        public TreeNode(int val) {
            this.val = val;
        }

    }
}
