package com.dream.tree.traversal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fanrui
 * @time 2020-03-20 23:21:46
 * 按层次遍历的同时，区分每一层
 * LeetCode 102： https://leetcode-cn.com/problems/binary-tree-level-order-traversal/
 * 剑指 Offer 32：https://leetcode-cn.com/problems/cong-shang-dao-xia-da-yin-er-cha-shu-ii-lcof/
 */
public class TraversalByLevelSaveLevel {

    // 思路一：将 Node 和 层进行包装
    public List<List<Integer>> levelOrder1(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }

        LinkedList<Entry> queue = new LinkedList<>();
        queue.add(new Entry(root, 0));

        List<List<Integer>> res = new ArrayList<>();
        while (!queue.isEmpty()) {
            Entry curEntry = queue.pollFirst();
            TreeNode curNode = curEntry.node;
            List<Integer> resLevelList;
            // 当前层数在 res 中存在，从 res 中获取当前对应的 list
            if (curEntry.level == res.size() - 1) {
                resLevelList = res.get(curEntry.level);
            } else {
                // 当前层数在 res 中不存在，则 new 一个新的 list
                resLevelList = new ArrayList<>();
                res.add(resLevelList);
            }
            resLevelList.add(curNode.val);
            if (curNode.left != null) {
                queue.add(new Entry(curNode.left, curEntry.level + 1));
            }
            if (curNode.right != null) {
                queue.add(new Entry(curNode.right, curEntry.level + 1));
            }
        }
        return res;
    }


    // 思路二：记录每一层有多少个元素，从而做到区分每一层
    public List<List<Integer>> levelOrder(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }
        LinkedList<TreeNode> queue = new LinkedList<>();
        List<List<Integer>> res = new ArrayList<>();
        queue.add(root);
        int curLevel = 0;
        // 当前行的剩余元素个数
        int curLevelRetain = 1;
        // 下一行的元素个数
        int nextLevelCount = 0;

        while (!queue.isEmpty()) {
            TreeNode curNode = queue.pollFirst();
            curLevelRetain--;
            List<Integer> resLevelList;
            // 当前层数在 res 中存在，从 res 中获取当前对应的 list
            if (curLevel == res.size() - 1) {
                resLevelList = res.get(curLevel);
            } else {
                // 当前层数在 res 中不存在，则 new 一个新的 list
                resLevelList = new ArrayList<>();
                res.add(resLevelList);
            }
            resLevelList.add(curNode.val);
            if (curNode.left != null) {
                nextLevelCount++;
                queue.add(curNode.left);
            }
            if (curNode.right != null) {
                nextLevelCount++;
                queue.add(curNode.right);
            }

            // 当前层遍历结束
            if (curLevelRetain == 0) {
                curLevel++;
                curLevelRetain = nextLevelCount;
                nextLevelCount = 0;
            }
        }

        return res;
    }
}

class Entry {
    TreeNode node;
    int level;

    public Entry(TreeNode node, int level) {
        this.node = node;
        this.level = level;
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int x) {
        val = x;
    }
}