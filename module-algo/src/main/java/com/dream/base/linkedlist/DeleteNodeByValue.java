package com.dream.base.linkedlist;


/**
 * @author fanrui
 * @time 2020-03-19 23:01:13
 * 删除 value 为给定值的节点
 * LeetCode ：https://leetcode-cn.com/problems/shan-chu-lian-biao-de-jie-dian-lcof/
 */
public class DeleteNodeByValue {


    public ListNode deleteNode(ListNode head, int val) {
        ListNode cur = head;
        ListNode next = null;

        // 如果删除的是头节点
        if (head.val == val) {
            head = head.next;
            return head;
        }

        // 如果删除的是非头节点
        while (cur != null) {
            next = cur.next;
            if (next != null && next.val == val) {
                break;
            }
            cur = cur.next;
        }

        if (cur != null) {
            cur.next = next.next;
            next = null;
        }
        return head;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

}
