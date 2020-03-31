package com.dream.tree.algo;

/**
 * @author fanrui
 * 判断一棵树是否是另一棵树的子树
 */
public class CheckSubTree {

    // 思路一：序列化树，然后根据字符串去判断
    public boolean checkSubTree1(TreeNode t1, TreeNode t2) {

        StringBuilder sb = new StringBuilder();
        serialByPre(t1, sb);
        String tree1 = sb.toString();

        sb = new StringBuilder();
        serialByPre(t2, sb);
        String tree2 = sb.toString();

        int res = tree1.indexOf(tree2);
        return res != -1;
    }

    public void serialByPre(TreeNode head, StringBuilder sb) {
        // 保存 null 值
        if (head == null) {
            sb.append("#_");
            return;
        }
        sb.append(head.val).append("_");
        serialByPre(head.left, sb);
        serialByPre(head.right, sb);
    }


    // 思路二：递归遍历左右子树
    public boolean checkSubTree(TreeNode t1, TreeNode t2) {

        if (t1 == null && t2 == null) {
            return true;
        }

        if (t1 == null || t2 == null) {
            return false;
        }

        return isSame(t1, t2) || checkSubTree(t1.left, t2) || checkSubTree(t1.right, t2);

    }

    public boolean isSame(TreeNode t1, TreeNode t2) {

        // 两个都是 null，则返回 true
        if (t1 == null && t2 == null) {
            return true;
        }

        // 只有其他一个为 null，返回 false
        if (t1 == null || t2 == null) {
            return false;
        }

        return t1.val == t2.val && isSame(t1.left, t2.left) && isSame(t1.right, t2.right);
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
