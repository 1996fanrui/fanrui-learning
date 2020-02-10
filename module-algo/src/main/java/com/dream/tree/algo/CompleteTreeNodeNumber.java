package com.dream.tree.algo;

/**
 * @author fanrui
 * 求完全二叉树的节点个数，要求 时间复杂度低于O（n）
 */
public class CompleteTreeNodeNumber {

    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    public static int nodeNum(Node head) {
        if (head == null) {
            return 0;
        }
        return bs(head, 1, mostLeftLevel(head, 1));
    }

    /**
     *
     * @param node 当前节点
     * @param level 当前 node 节点在第几层
     * @param h 整个树的高度，在整个计算过程中 h 保持不变
     * @return 以 node 为根节点的树的节点数
     */
    public static int bs(Node node, int level, int h) {
        // node 在第 level 层，且树的高度为 level，
        // 表示当前 node 节点为叶子节点，
        // 以 node 为根节点的树的节点数为 1
        if (level == h) {
            return 1;
        }

        // 当前节点右子树的最左节点如果到达了最后一层，那么当前节点的左子树肯定是一颗满二叉树
        // 左子树 满二叉树的节点个数为 (1 << (h - level)) - 1，再加上当前节点就是  (1 << (h - level))
        // 然后再递归遍历右子树即可
        if (mostLeftLevel(node.right, level + 1) == h) {
            return (1 << (h - level)) + bs(node.right, level + 1, h);
        } else {
            // 当前节点右子树的最左节点如果没有到达最后一层，那么当前节点的右子树肯定是一颗满二叉树
            // 右子树 满二叉树的节点个数为 (1 << (h - level - 1)) - 1，再加上当前节点就是  (1 << (h - level - 1))
            // 然后再递归遍历左子树即可
            return (1 << (h - level - 1)) + bs(node.left, level + 1, h);
        }
    }

    public static int mostLeftLevel(Node node, int level) {
        while (node != null) {
            level++;
            node = node.left;
        }
        return level - 1;
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.left = new Node(2);
        head.right = new Node(3);
        head.left.left = new Node(4);
        head.left.right = new Node(5);
        head.right.left = new Node(6);
        System.out.println(nodeNum(head));

    }

}
