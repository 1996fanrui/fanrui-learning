package com.dream.tree.algo.recursion;


/**
 * @author fanrui
 * 求树的高度
 * 递归遍历，左右子树，拿到左树高度，拿到右树高度。最大值 +1 即可
 */
public class TreeHeight {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public static int getTreeHeight(Node head){
        if(head == null){
            return 0;
        }
        // 拿到左树高度，拿到右树高度。最大值 +1
        int leftHeight = getTreeHeight(head.left);
        int rightHeight = getTreeHeight(head.right);
        return Math.max(leftHeight, rightHeight) + 1;
    }




}
