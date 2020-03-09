package com.dream.tree.algo.recursion;
import java.io.*;

/**
 * @author fanrui
 * 	二叉树节点间的最大距离问题
 * 	牛客链接：https://www.nowcoder.com/practice/88331be6da0d40749b068586dc0a2a8b?tpId=101&tqId=33247&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 * 	LeetCode 543 二叉树的直径：https://leetcode-cn.com/problems/diameter-of-binary-tree/
 */
public class MaxDistanceInTree {

    public int diameterOfBinaryTree(TreeNode root) {
        if(root == null){
            return 0;
        }
        return process(root).curDiameter - 1;
    }

    private ReturnType process(TreeNode head){
        if(null == head){
            return ReturnType.NULL_RETURN;
        }

        // 计算左右子树 的 高度和直径
        ReturnType leftRes = process(head.left);
        ReturnType rightRes = process(head.right);

        int curDiameter = leftRes.height + rightRes.height + 1;
        int curHeight = Math.max(leftRes.height, rightRes.height) + 1;
        curDiameter = Math.max(curDiameter,
                Math.max(leftRes.curDiameter, rightRes.curDiameter));

        return new ReturnType(curHeight, curDiameter);
    }


    public static class ReturnType{
        int height;
        int curDiameter;

        public static ReturnType NULL_RETURN = new ReturnType(0,0);

        public ReturnType(int height, int curDiameter) {
            this.height = height;
            this.curDiameter = curDiameter;
        }
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }


    public static void main (String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
        TreeNode root = buildTree(br);
        int res = new MaxDistanceInTree().diameterOfBinaryTree(root);
        System.out.println(res);
    }

    public static TreeNode buildTree (BufferedReader br) throws IOException {
        String[] str = br.readLine().split(" ");
        int[] nodes = new int[str.length];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = Integer.parseInt(str[i]);
        }
        TreeNode root = new TreeNode(nodes[0]);
        if (nodes[1] != 0) {
            root.left = buildTree(br);
        }
        if (nodes[2] != 0) {
            root.right = buildTree(br);
        }
        return root;
    }
}
