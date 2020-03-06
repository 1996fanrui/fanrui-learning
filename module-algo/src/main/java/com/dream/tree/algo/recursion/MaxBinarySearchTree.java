package com.dream.tree.algo.recursion;

/**
 * @author fanrui
 * 找最大搜索二叉树
 */
public class MaxBinarySearchTree {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    private static class ReturnType{

        Node BSTHead;
        int size;
        long max;
        long min;

        public ReturnType(Node BSTHead, int size, long max, long min) {
            this.BSTHead = BSTHead;
            this.size = size;
            this.max = max;
            this.min = min;
        }
        public ReturnType(int size, Node BSTHead, long min, long max) {
            this.BSTHead = BSTHead;
            this.size = size;
            this.max = max;
            this.min = min;
        }
    }


    public static Node getMaxBST(Node head){
        return process(head).BSTHead;
    }

    public static int getMaxBSTSize(Node head){
        return process(head).size;
    }


    private static ReturnType process(Node head) {
        if(head==null) {
            return new ReturnType(null, 0, Long.MIN_VALUE, Long.MAX_VALUE);
        }

        ReturnType leftRes = process(head.left);
        ReturnType rightRes = process(head.right);

        // 满足这四个条件，则 head 对应的整棵树都是搜索二叉树
        // 1、 左子树的最大搜索二叉树是 左孩子
        // 2、 右子树的最大搜索二叉树是 右孩子
        // 3、 当前节点大于左子树的最大节点
        // 4、 当前节点小于右子树的最小节点
        if(leftRes.BSTHead == head.left
                && rightRes.BSTHead == head.right
                && leftRes.max < head.value
                && rightRes.min > head.value ){
            return new ReturnType(head,leftRes.size + 1 + rightRes.size,
                    Math.max(rightRes.max, head.value),
                    Math.min(leftRes.min, head.value));
        }

        // 当前节点不是 BST

        Node returnHead;
        int returnSize;
        if (leftRes.size >= rightRes.size) {
            returnHead = leftRes.BSTHead;
            returnSize = leftRes.size;
        } else {
            returnHead = rightRes.BSTHead;
            returnSize = rightRes.size;
        }

        return new ReturnType(returnHead, returnSize,
                0, 0);
    }
}
