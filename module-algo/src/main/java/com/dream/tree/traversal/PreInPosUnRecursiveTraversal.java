package com.dream.tree.traversal;

import java.util.Stack;

/**
 * @author fanrui
 */
public class PreInPosUnRecursiveTraversal {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }


    public static void preOrderUnRecur(Node head) {
        System.out.print("先序遍历: ");
        if (head != null) {
            Stack<Node> stack = new Stack<Node>();
            //  根节点 压栈
            stack.add(head);
            // 只要栈不空，则一直循环
            while (!stack.isEmpty()) {
                // 出栈，并打印遍历
                head = stack.pop();
                System.out.print(head.value + " ");
                // 右孩子不为空，则压栈
                if (head.right != null) {
                    stack.push(head.right);
                }
                // 左孩子不为空，则压栈
                if (head.left != null) {
                    stack.push(head.left);
                }
            }
        }
        System.out.println();
    }

    /**
     * 整体流程： 顺着左孩子，一直压栈，
     * 当到了最左时，出栈遍历出栈的元素，并开始当前节点的处理右子树，
     * 遍历右子树的流程也是类似，顺着左孩子，一直压栈，到了最左时。。。
     * @param head
     */
    public static void inOrderUnRecur(Node head) {
        System.out.print("中序遍历: ");
        if (head != null) {
            Stack<Node> stack = new Stack<Node>();
            Node cur = head;
            while (!stack.isEmpty() || cur != null) {
                // 当前节点不为空，则将当前节点 压栈，当前节点 指向 左孩子
                if (cur != null) {
                    stack.push(cur);
                    cur = cur.left;
                } else {
                    // 当前节点为空，则从栈中弹出数据并遍历，
                    // 然后当前节点指向 右孩子，即：遍历右子树
                    cur = stack.pop();
                    System.out.print(cur.value + " ");
                    cur = cur.right;
                }
            }
        }
        System.out.println();
    }


    /**
     * 借助了先序遍历的思想，先序遍历：中 左 右， 后序遍历：左 右 中
     * 我们可以把先序遍历的左右代码改写，则先序遍历可以变成：中 右 左
     * 栈可以将一堆数据逆序，所以：中 右 左 的遍历过程将所有遍历的元素依次全部放到一个栈中，
     * 再依次弹出，就会变成 左 右 中
     *
     * @param head
     */
    public static void posOrderUnRecur1(Node head) {
        System.out.print("后序遍历: ");
        if (head != null) {
            // s1 就是先序遍历需要用到的栈
            Stack<Node> s1 = new Stack<Node>();
            // s2 是用于将先序遍历的数据进行逆序
            Stack<Node> s2 = new Stack<Node>();
            s1.push(head);
            while (!s1.isEmpty()) {
                head = s1.pop();
                // 跟前序遍历类似，只不过这里是先将数据入栈，暂存到 s2 中
                s2.push(head);
                if (head.left != null) {
                    s1.push(head.left);
                }
                if (head.right != null) {
                    s1.push(head.right);
                }
            }
            // s2 出栈，遍历
            while (!s2.isEmpty()) {
                System.out.print(s2.pop().value + " ");
            }
        }
        System.out.println();
    }

    public static void posOrderUnRecur2(Node h) {
        System.out.print("pos-order: ");
        if (h != null) {
            Stack<Node> stack = new Stack<Node>();
            stack.push(h);
            Node c = null;
            while (!stack.isEmpty()) {
                c = stack.peek();
                if (c.left != null && h != c.left && h != c.right) {
                    stack.push(c.left);
                } else if (c.right != null && h != c.right) {
                    stack.push(c.right);
                } else {
                    System.out.print(stack.pop().value + " ");
                    h = c;
                }
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Node head = new Node(5);
        head.left = new Node(3);
        head.right = new Node(8);
        head.left.left = new Node(2);
        head.left.right = new Node(4);
        head.left.left.left = new Node(1);
        head.right.left = new Node(7);
        head.right.left.left = new Node(6);
        head.right.right = new Node(10);
        head.right.right.left = new Node(9);
        head.right.right.right = new Node(11);

        // unrecursive
        System.out.println("============unrecursive=============");
        preOrderUnRecur(head);
        inOrderUnRecur(head);
        posOrderUnRecur1(head);
        posOrderUnRecur2(head);

    }
}
