package com.dream.tree.traversal;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author fanrui
 * @time 2020-02-08 21:53:23
 */
public class TraversalByLevel {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public static void traversalByLevel(Node head) {
        System.out.print("按层次遍历: ");
        if (head == null) {
            return;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.offer(head);
        Node current;
        while (!queue.isEmpty()) {
            // 出队并遍历
            current = queue.poll();
            System.out.println(current.value);
            //如果当前节点的左孩子不为空，则左孩子入队
            if (current.left != null) {
                queue.offer(current.left);
            }
            //如果当前节点的右孩子不为空，把右孩子入队
            if (current.right != null) {
                queue.offer(current.right);
            }
        }
    }
}
