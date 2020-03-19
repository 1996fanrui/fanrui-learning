package com.dream.base.linkedlist;

/**
 * @author fanrui
 * @time 2020-03-20 01:47:14
 * 合并两个排序的链表
 * 剑指 Offer 25：https://leetcode-cn.com/problems/he-bing-liang-ge-pai-xu-de-lian-biao-lcof/
 * LeetCode 21：https://leetcode-cn.com/problems/merge-two-sorted-lists/
 */
public class MergeTwoSortedList {

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        // 搞个哨兵，便于编码
        ListNode resList = new ListNode(0);
        ListNode cur = resList;

        // 找到两个链表头部的较小值，串在 cur 的 next
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        // 将 非空链表串在 cur 的 next
        if (l1 == null) {
            cur.next = l2;
        } else {
            cur.next = l1;
        }
        return resList.next;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

}
