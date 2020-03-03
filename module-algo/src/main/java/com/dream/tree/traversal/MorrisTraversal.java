package com.dream.tree.traversal;

public class MorrisTraversal {


    public static class Node {
        public int value;
        Node left;
        Node right;

        public Node(int data) {
            this.value = data;
        }
    }


    /**
     * Morris 遍历的原型
     * @param root
     */
    public static void morris(Node root){

        if(root == null){
            return;
        }

        // 从根节点开始遍历
        Node cur = root;
        Node mostRight;

        while (cur != null) {
            if(cur.left != null) {
                // 找左子树的最右节点，注：mostRight 的 right 为 cur 时，也表示找到了最右节点
                mostRight = cur.left;
                while (mostRight.right != null && mostRight.right != cur) {
                    mostRight = mostRight.right;
                }

                // mostRight 的 right 为 null，表示第一次遍历左子树
                if(mostRight.right == null){
                    // 将当前节点串在 左子树最右节点的 right 指针
                    mostRight.right = cur;
                    // cur 左移，开始遍历左子树
                    cur = cur.left;
                    continue;
                } else {
                    // mostRight 的 right 不为 null，表示第二次遍历到了 cur，
                    // 需要将之间附加的指针去掉
                    mostRight.right = null;
                }
            }
            // cur 节点右移
            cur = cur.right;
        }
    }


    /**
     * Morris 中序遍历
     * 遍历时机： cur 指针右移时遍历
     * @param root
     */
    public static void morrisIn(Node root){

        if(root == null){
            return;
        }

        // 从根节点开始遍历
        Node cur = root;
        Node mostRight;

        while (cur != null) {
            if(cur.left != null) {
                // 找左子树的最右节点，注：mostRight 的 right 为 cur 时，也表示找到了最右节点
                mostRight = cur.left;
                while (mostRight.right != null && mostRight.right != cur) {
                    mostRight = mostRight.right;
                }

                // mostRight 的 right 为 null，表示第一次遍历左子树
                if(mostRight.right == null){
                    // 将当前节点串在 左子树最右节点的 right 指针
                    mostRight.right = cur;
                    // cur 左移，开始遍历左子树
                    cur = cur.left;
                    continue;
                } else {
                    // mostRight 的 right 不为 null，表示第二次遍历到了 cur，
                    // 需要将之间附加的指针去掉
                    mostRight.right = null;
                }
            }
            // here, cur 右移说明左子树遍历完了，要遍历右子树了，
            // 所以在这里遍历就是中序遍历
            System.out.println(cur.value);
            // cur 节点右移
            cur = cur.right;
        }
    }

    /**
     * Morris 先序遍历
     * 两处遍历： 1、 当 cur 指针左移时
     *          2、 当 cur 没有左孩子时
     * @param root
     */
    public static void morrisPre(Node root){

        if(root == null){
            return;
        }

        // 从根节点开始遍历
        Node cur = root;
        Node mostRight;

        while (cur != null) {
            if(cur.left != null) {
                // 找左子树的最右节点，注：mostRight 的 right 为 cur 时，也表示找到了最右节点
                mostRight = cur.left;
                while (mostRight.right != null && mostRight.right != cur) {
                    mostRight = mostRight.right;
                }

                // mostRight 的 right 为 null，表示第一次遍历左子树
                if(mostRight.right == null){
                    // 将当前节点串在 左子树最右节点的 right 指针
                    mostRight.right = cur;
                    // here1, cur 左移说明要开始遍历左子树了，
                    // 所以在这里遍历就是先序遍历
                    System.out.println(cur.value);
                    // cur 左移，开始遍历左子树
                    cur = cur.left;
                    continue;
                } else {
                    // mostRight 的 right 不为 null，表示第二次遍历到了 cur，
                    // 需要将之间附加的指针去掉
                    mostRight.right = null;
                }
            } else {
                // here2, 有些节点可能没有左孩子，所以当没有左孩子时，遍历该节点
                System.out.println(cur.value);
            }
            // cur 节点右移
            cur = cur.right;
        }
    }

    /**
     * Morris 后序遍历
     * 在第二次来到这个节点时，逆序打印左子树的右边界即可。
     * @param root
     */
    public static void morrisPos(Node root){

        if(root == null){
            return;
        }

        // 从根节点开始遍历
        Node cur = root;
        Node mostRight;

        while (cur != null) {
            if(cur.left != null) {
                // 找左子树的最右节点，注：mostRight 的 right 为 cur 时，也表示找到了最右节点
                mostRight = cur.left;
                while (mostRight.right != null && mostRight.right != cur) {
                    mostRight = mostRight.right;
                }

                // mostRight 的 right 为 null，表示第一次遍历左子树
                if(mostRight.right == null){
                    // 将当前节点串在 左子树最右节点的 right 指针
                    mostRight.right = cur;
                    // cur 左移，开始遍历左子树
                    cur = cur.left;
                    continue;
                } else {
                    // mostRight 的 right 不为 null，表示第二次遍历到了 cur，
                    // 需要将之间附加的指针去掉
                    mostRight.right = null;

                    // 这里逆序遍历左子树的右边界
                    printRightEdge(cur.left);
                }
            }
            // cur 节点右移
            cur = cur.right;
        }

        // 逆序遍历整棵树的右边界
        printRightEdge(root);
    }


    /**
     * 逆序打印左子树的右边界，
     * 关键在于如何做到空间复杂度 O（1）？
     *  类似于单链表反转，将数右边界逆序后，遍历，再逆序回来
     * @param head
     */
    private static void printRightEdge(Node head) {
        Node tail = reverseRightEdge(head);
        Node cur = tail;
        while (cur!=null){
            System.out.println(cur.value);
            cur = cur.right;
        }
        reverseRightEdge(tail);
    }


    /**
     * 利用单链表反转的原理，反转树的右边界
     * @param head
     * @return
     */
    private static Node reverseRightEdge(Node head) {
        Node newTree = null;
        Node next = null;

        while( head != null){
            next = newTree.right;
            head.right = newTree;
            newTree = head;
            head = next;
        }
        return newTree;
    }


}
