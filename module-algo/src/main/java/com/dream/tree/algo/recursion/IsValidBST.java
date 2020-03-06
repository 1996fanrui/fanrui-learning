package com.dream.tree.algo.recursion;


import java.util.Stack;

/**
 * @author fanrui
 * 验证一棵树是否是二叉搜索树
 * 注：这种比较大小的题，会有一个坑：
 * 如果数据是 int 类型，一般我们初始值可能会赋值一个 Integer.MIN_VALUE,  Integer.MAX_VALUE。
 * 但是如果数据真存在这两个边界值，可能导致结果错误
 */
public class IsValidBST {

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public static boolean isValidBST(TreeNode root) {
//        // 使用递归方式来做
//        return process(root).isValidBST;
        // 使用非递归版的 中序遍历来做
        return isValidBSTByInTraversal(root);
    }



    // 中序遍历
    private static boolean isValidBSTByInTraversal(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        TreeNode cur = root;
        // 保存前一个遍历的值，注：一定要用 Long ，而不是 int
        long preValue = Long.MIN_VALUE;
        while (!stack.isEmpty() || cur!=null) {
            if(cur !=null){
                stack.push(cur);
                cur = cur.left;
            } else {
                cur = stack.pop();
                // 遍历，发现当前值不比前一个值大，直接返回 false
                if(cur.val <= preValue){
                    return false;
                }
                preValue = cur.val;
                cur = cur.right;
            }
        }
        return true;
    }



    // 递归方式来做
    private static class ReturnType {

        // 防止 false new 很多相同的对象
        public static final ReturnType FALSE_RETURN = new ReturnType(false,0,0);

        boolean isValidBST;

        // max 和 min 用 long，是为了避开 int 的最大值坑
        long max;
        long min;

        public ReturnType(boolean isValidBST, long max, long min) {
            this.isValidBST = isValidBST;
            this.max = max;
            this.min = min;
        }
    }



    private static ReturnType process(TreeNode head){
        if(head == null){
            return new ReturnType(true,
                    Long.MIN_VALUE, Long.MAX_VALUE);
        }

        // 计算左子树的返回值
        ReturnType leftReturn = process(head.left);

        // 如果左子树非 BST 或 当前节点小于左子树最大值，
        // 没必要对右子树进行运算，直接返回 false
        if(!leftReturn.isValidBST
                || head.val < leftReturn.max){
            return ReturnType.FALSE_RETURN;
        }

        ReturnType rightReturn = process(head.right);

        // 左右子树都是 BST，且当前节点 大于 左子树最大值，小于 右子树最小值，则当前 head 为 BST
        if(leftReturn.isValidBST
                && rightReturn.isValidBST
                && head.val > leftReturn.max
                && head.val < rightReturn.min){
            return new ReturnType(true, Math.max(head.val, rightReturn.max),
                    Math.min(head.val, leftReturn.min));
        }

        return ReturnType.FALSE_RETURN;
    }


}
