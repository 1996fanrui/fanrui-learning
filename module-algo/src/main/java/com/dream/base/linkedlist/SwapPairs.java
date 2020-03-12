package com.dream.base.linkedlist;


/**
 * @author fanrui
 * @time 2020-03-12 12:40:52
 * 两两交换链表中的节点
 * LeetCode 24： https://leetcode-cn.com/problems/swap-nodes-in-pairs/
 *
 */
public class SwapPairs {

    public ListNode swapPairs(ListNode head) {
        ListNode cur = new ListNode(0);
        cur.next = head;
        head = cur;

        while (cur.next != null && cur.next.next != null) {
            ListNode firstNode = cur.next;
            ListNode secondNode = firstNode.next;
            cur.next = secondNode;
            firstNode.next = secondNode.next;
            secondNode.next = firstNode;
            cur = firstNode;
        }
        return head.next;
    }


    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
