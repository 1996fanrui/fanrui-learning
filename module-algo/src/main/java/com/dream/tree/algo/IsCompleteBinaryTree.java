package com.dream.tree.algo;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author fanrui
 */
public class IsCompleteBinaryTree {


    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public static boolean isCompleteBinaryTree(Node node){

        if(node == null){
            return true;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.offer(node);
        // 状态值表示是否遍历叶子节点
        boolean status = false;
        Node cur;
        while (!queue.isEmpty()){
            cur = queue.poll();

            // 节点有右孩子，没有左孩子，一定不是完全二叉树
            if(cur.left == null && cur.right != null){
                return false;
            }
            // 左孩子不为空，加队列，判断 status
            if(cur.left != null){
                queue.offer(cur.left);
                if(status){
                    return false;
                }
            } else {
                status = true;
            }

            // 左孩子不为空，加队列，判断 status
            if(cur.right != null){
                queue.offer(cur.right);
                if(status){
                    return false;
                }
            } else {
                status = true;
            }
        }
        return true;
    }


    //  方法2
    public static boolean isCompleteBinaryTree2(Node node){

        if(node == null){
            return true;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.offer(node);
        // 状态值表示是否遍历叶子节点
        boolean status = false;
        Node cur;
        Node l = null;
        Node r = null;
        while (!queue.isEmpty()){
            cur = queue.poll();
            l = cur.left;
            r = cur.right;
            // 遍历叶节点的过程中，如果出现孩子不为空
            // 或者出现了节点有右孩子，没有左孩子，
            // 一定不是完全二叉树，直接返回 false
            if ((status && (l != null || r != null))
                    || (l == null && r != null)) {
                return false;
            }
            if (l != null) {
                queue.offer(l);
            }
            if (r != null) {
                queue.offer(r);
            } else {
                // 出现了有一个孩子，或者无孩子，
                // 那么都要开始遍历叶节点了
                // 有一个孩子，也必定是仅有左孩子，如果仅有右孩子上面会直接返回 false
                // 所以出现了有一个孩子，或者无孩子，都是 右孩子为 null
                status = true;
            }
        }
        return true;
    }



    public static void main(String[] args) {
        Node head = new Node(6);
        head.right = new Node(3);

        System.out.println(isCompleteBinaryTree(head));
    }

}
