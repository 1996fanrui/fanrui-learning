package com.dream.tree.algo;

/**
 * @author fanrui
 * @time 2020-03-13 01:18:46
 * 二叉搜索树的最近公共祖先
 * LeetCode：
 */
public class LowestCommonAncestorOfBST {


    // 思路一：直接查找
    public TreeNode lowestCommonAncestor1(TreeNode root, TreeNode p, TreeNode q) {

        TreeNode cur = root;

        if(p == q){
            return p;
        }

        TreeNode small;
        TreeNode big;

        if(p.val > q.val){
            small = q;
            big = p;
        } else {
            small = p;
            big = q;
        }

        // 当前节点 val 大于小数的 value，小于 大数的 value，则退出循环，否则一直循环
        while ( !(cur.val >= small.val && cur.val <= big.val)){
            // 只要能进循环，说明两个节点在 cur 节点的同一颗子树

            // 两个节点都在左子树
            if(cur.val > small.val){
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return cur;
    }

    // 思路二：递归查找
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // p 和 q 都比当前节点大，往右找
        if (root.val < p.val && root.val < q.val) {
            return lowestCommonAncestor(root.right, p, q);
        } else if (root.val > p.val && root.val > q.val) {
            // p 和 q 都比当前节点小，往左找
            return lowestCommonAncestor(root.left, p, q);
        } else {
            // 否则，返回自己
            return root;
        }
    }


    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }


}
