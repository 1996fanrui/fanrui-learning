package com.dream.base.linkedlist;


/**
 * @author fanrui
 * @time 2020-03-20 01:09:15
 * 查找链表的倒数第 k 个节点
 * LeetCode 22：https://leetcode-cn.com/problems/lian-biao-zhong-dao-shu-di-kge-jie-dian-lcof/
 */
public class FindLastKthNode {

    public ListNode getKthFromEnd(ListNode head, int k) {
        if (k <= 0 || head == null) {
            return null;
        }
        ListNode fast = head;
        ListNode slow = head;
        // 快指针先走 k-1 步
        for (int i = 0; i < k - 1; i++) {
            fast = fast.next;
            if (fast == null) {
                return null;
            }
        }

        // fast 的 next 指针为 null 时，slow 指针就是要找的 节点
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }
        return slow;
    }


    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

}
