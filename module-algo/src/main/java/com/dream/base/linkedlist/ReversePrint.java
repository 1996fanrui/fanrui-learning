package com.dream.base.linkedlist;

import java.util.Stack;

/**
 * @author fanrui
 * @time 2020-03-18 19:03:24
 * 从尾到头打印链表
 * LeetCode ：https://leetcode-cn.com/problems/cong-wei-dao-tou-da-yin-lian-biao-lcof/
 */
public class ReversePrint {

    int listLength = 0;

    // 思路一：反转链表、打印、再反转回来
    public int[] reversePrint1(ListNode head) {
        ListNode rev = reverseList(head);
        ListNode cur = rev;
        int[] res = new int[listLength];
        for (int i = 0; i < listLength; i++) {
            res[i] = cur.val;
            cur = cur.next;
        }
        reverseList(rev);
        return res;
    }


    private ListNode reverseList(ListNode head) {
        listLength = 0;
        ListNode newList = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = newList;
            newList = cur;
            cur = next;
            listLength++;
        }
        return newList;
    }


    // 思路二：使用 栈来实现从后往前打印
    public int[] reversePrint2(ListNode head) {
        Stack<ListNode> stack = new Stack<>();
        ListNode cur = head;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }
        int[] res = new int[stack.size()];
        int i = 0;
        while (!stack.isEmpty()) {
            res[i++] = stack.pop().val;
        }
        return res;
    }

    // 思路三：递归
    public int[] reversePrint(ListNode head) {
        ListNode cur = head;
        int listCount = 0;
        while (cur != null) {
            listCount++;
            cur = cur.next;
        }
        int[] res = new int[listCount];
        recPrint(head, res, 0, listCount - 1);
        return res;
    }

    private void recPrint(ListNode cur, int[] res, int curIndex, int totalCount) {
        if (cur == null) {
            return;
        }
        recPrint(cur.next, res, curIndex + 1, totalCount);
        res[totalCount - curIndex] = cur.val;
    }


    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
