package com.dream.base.linkedlist;

/**
 * @author fanrui
 * @time 2020-03-22 00:47:51
 * 假设链表无环，两个单链表相交的话，找出相交的第一个节点
 * LeetCode 160： https://leetcode-cn.com/problems/intersection-of-two-linked-lists/
 * 剑指 Offer 52：https://leetcode-cn.com/problems/liang-ge-lian-biao-de-di-yi-ge-gong-gong-jie-dian-lcof/
 */
public class FindFirstIntersectNodeWithoutLoop {

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    public ListNode getIntersectionNode(ListNode head1, ListNode head2) {
        if (head1 == null || head2 == null) {
            return null;
        }

        ListNode cur1 = head1;
        ListNode cur2 = head2;
        int diff = 0;
        while (cur1.next != null) {
            diff++;
            cur1 = cur1.next;
        }
        while (cur2.next != null) {
            diff--;
            cur2 = cur2.next;
        }
        // 尾节点不等，直接 返回 null
        if (cur1 != cur2) {
            return null;
        }

        // cur1 is long linked list ,cur2 is short
        cur1 = diff > 0 ? head1 : head2;
        cur2 = cur1 == head1 ? head2 : head1;

        // long list step n
        diff = Math.abs(diff);
        while (diff != 0) {
            diff--;
            cur1 = cur1.next;
        }
        // two list together iteration
        while (cur1 != cur2) {
            cur1 = cur1.next;
            cur2 = cur2.next;
        }
        return cur1;
    }

}
