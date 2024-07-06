package com.dream.tree.algo;


/**
 * @author fanrui
 * 二叉树的最近公共祖先
 * LeetCode 236 : https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-tree/
 *
 */
public class LowestCommonAncestor {

    private TreeNode res = null;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        res = null;
        recurseTree(root, p, q);
        return res;
    }

    private boolean recurseTree(TreeNode root, TreeNode p, TreeNode q) {
        if(null != res || root == null){
            return false;
        }

        // 左子树是否包含 pq，右子树是否包含 pq，当前节点是否是 pq
        int leftRes = recurseTree(root.left, p, q) ? 1 : 0;
        int rightRes = recurseTree(root.right, p, q) ? 1 : 0;
        int curRes = (root == p || root == q) ? 1 : 0;

        // 满足其中两个条件，则 找到结果
        if( leftRes + rightRes + curRes >= 2){
            res = root;
        }

        // 含有其中一个就返回 true
        return (leftRes + rightRes + curRes > 0);
    }

    // 方案 2
    private static final ResultType NOT_EXIST_RESULT = new ResultType(null, 0);

    public TreeNode lowestCommonAncestor1(TreeNode root, TreeNode p, TreeNode q) {
        return findNode(root, p, q).result;
    }

    private ResultType findNode(TreeNode cur, TreeNode p, TreeNode q) {
        if (cur == null) {
            return NOT_EXIST_RESULT;
        }

        ResultType leftResult = findNode(cur.left, p, q);
        if(leftResult.result != null) {
            return leftResult;
        }
        ResultType rightResult = findNode(cur.right, p, q);
        if(rightResult.result != null) {
            return rightResult;
        }
        int count = 0;
        if(cur.val == p.val || cur.val == q.val) {
            count++;
        }
        count += leftResult.count;
        count += rightResult.count;

        return count == 2 ? new ResultType(cur, 2) : new ResultType(null, count);
    }

    private static class ResultType {
        final TreeNode result;
        // how many p or q are found from current node or its children.
        final int count;

        ResultType(TreeNode result, int count) {
            this.result = result;
            this.count = count;
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
