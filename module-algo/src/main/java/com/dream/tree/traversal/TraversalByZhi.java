package com.dream.tree.traversal;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author fanrui
 * @time 2020-03-20 23:47:58
 * 之字形打印二叉树（按层次遍历，第一行从左到右，第二行从右到左，依次类推）
 * 剑指 Offer 32-III ： https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-iii-lcof/
 */
public class TraversalByZhi {

    public List<List<Integer>> levelOrder(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }

        Stack<TreeNode> curStack = new Stack<>();
        Stack<TreeNode> nextStack = new Stack<>();

        curStack.add(root);
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> curLevelList = new ArrayList<>();
        res.add(curLevelList);
        boolean leftPrior = true;

        while (!curStack.isEmpty()) {
            TreeNode curNode = curStack.pop();
            // 遍历当前 node，将当前 node 添加到 当前行的 list 中
            curLevelList.add(curNode.val);
            // 左右孩子添加到下一行的 nextStack 中
            if (leftPrior) {
                // 从左向右遍历的行，应该先添加 左孩子
                if (curNode.left != null) {
                    nextStack.push(curNode.left);
                }
                if (curNode.right != null) {
                    nextStack.push(curNode.right);
                }
            } else {
                // 从右向左遍历的行，应该先添加 右孩子
                if (curNode.right != null) {
                    nextStack.push(curNode.right);
                }
                if (curNode.left != null) {
                    nextStack.push(curNode.left);
                }
            }
            // curStack 为空，表示当前行遍历完了
            if (curStack.isEmpty()) {
                // 下一行没数据，直接结束
                if (nextStack.isEmpty()) {
                    break;
                }
                // 下一行有数据，curStack 指向 nextStack。且 结果链表添加一个 list。leftPrior 取反
                Stack<TreeNode> tmp = nextStack;
                nextStack = curStack;
                curStack = tmp;
                curLevelList = new ArrayList<>();
                res.add(curLevelList);
                leftPrior = !leftPrior;
            }
        }

        return res;
    }


    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }
}

