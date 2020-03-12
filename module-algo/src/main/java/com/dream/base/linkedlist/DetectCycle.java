package com.dream.base.linkedlist;

/**
 * @author fanrui
 * 判断链表是否有环，并返回入环的第一个节点
 * LeetCode 142： https://leetcode-cn.com/problems/linked-list-cycle-ii/
 */
public class DetectCycle {

    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }

        ListNode fast = head.next.next;
        ListNode slow = head.next;

        while (fast != null) {
            if (fast == slow) {
                fast = head;
                break;
            }
            if (fast.next == null) {
                return null;
            }
            fast = fast.next.next;
            slow = slow.next;
        }

        if (fast == null) {
            return null;
        }

        while (fast != slow) {
            fast = fast.next;
            slow = slow.next;
        }
        return fast;
    }


    private static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }
}
