package com.dream.tree.traversal;

/**
 * @author fanrui
 */
public class PreInPosRecursiveTraversal {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public static void preOrderRecur(Node head) {
        System.out.print("先序遍历: ");
        if (head == null) {
            return;
        }
        // 遍历自己
        System.out.print(head.value + " ");
        // 递归遍历左子树
        preOrderRecur(head.left);
        // 递归遍历右子树
        preOrderRecur(head.right);
    }

    public static void inOrderRecur(Node head) {
        System.out.print("中序遍历: ");
        if (head == null) {
            return;
        }
        // 递归遍历左子树
        preOrderRecur(head.left);
        // 遍历自己
        System.out.print(head.value + " ");
        // 递归遍历右子树
        preOrderRecur(head.right);
    }

    public static void posOrderRecur(Node head) {
        System.out.print("后序遍历: ");
        if (head == null) {
            return;
        }
        // 递归遍历左子树
        preOrderRecur(head.left);
        // 遍历自己
        System.out.print(head.value + " ");
        // 递归遍历右子树
        preOrderRecur(head.right);
    }

}
