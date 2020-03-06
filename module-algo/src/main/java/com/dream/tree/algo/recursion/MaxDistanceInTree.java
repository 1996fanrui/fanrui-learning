package com.dream.tree.algo.recursion;
import java.io.*;

/**
 * @author fanrui
 * 	二叉树节点间的最大距离问题
 * 	牛客链接：https://www.nowcoder.com/practice/88331be6da0d40749b068586dc0a2a8b?tpId=101&tqId=33247&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 */
public class MaxDistanceInTree {


    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }


    private static class ReturnType {
        public static final ReturnType NULL_RETURN = new ReturnType(0,0);
        int height;
        int maxDistance;

        public ReturnType(int height, int maxDistance) {
            this.height = height;
            this.maxDistance = maxDistance;
        }
    }


    public static int getMaxDistance(Node head) {
        return process(head).maxDistance;
    }


    private static ReturnType process(Node head) {
        if (head == null) {
            return ReturnType.NULL_RETURN;
        }

        ReturnType leftResult = process(head.left);
        ReturnType rightResult = process(head.right);

        int curMaxDistance = leftResult.height + rightResult.height + 1;
        curMaxDistance = Math.max(curMaxDistance,
                Math.max(leftResult.maxDistance, rightResult.maxDistance));

        return new ReturnType(Math.max(leftResult.height, rightResult.height) + 1
                , curMaxDistance);
    }


    public static void main (String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
        Node root = buildTree(br);
        int res = getMaxDistance(root);
        System.out.println(res);
    }

    public static Node buildTree (BufferedReader br) throws IOException {
        String[] str = br.readLine().split(" ");
        int[] nodes = new int[str.length];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = Integer.parseInt(str[i]);
        }
        Node root = new Node(nodes[0]);
        if (nodes[1] != 0) {
            root.left = buildTree(br);
        }
        if (nodes[2] != 0) {
            root.right = buildTree(br);
        }
        return root;
    }
}
