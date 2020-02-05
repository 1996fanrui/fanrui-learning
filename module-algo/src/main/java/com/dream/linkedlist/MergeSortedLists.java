package com.dream.linkedlist;

/**
 * 有序链表合并
 * @author fanrui
 * @time 2019-03-21 18:24:24
 * @desc 利用归并思想即可，每次拿两个链表头的较小的数 放到链表中
 */
public class MergeSortedLists {


    /**
     *
     * @desc 有序链表合并
     * @param la
     * @param lb
     * @return
     */
    public static Node mergeSortedLists(Node la, Node lb) {
        if (la == null) {
            return lb;
        }
        if (lb == null) {
            return la;
        }

        Node p = la;
        Node q = lb;
        Node head;
        // 初始化头节点
        if (p.data < q.data) {
            head = p;
            p = p.next;
        } else {
            head = q;
            q = q.next;
        }
        Node r = head;

        while (p != null && q != null) {
            if (p.data < q.data) {
                r.next = p;
                p = p.next;
            } else {
                r.next = q;
                q = q.next;
            }
            r = r.next;
        }

        if (p != null) {
            r.next = p;
        } else {
            r.next = q;
        }

        return head;
    }

}
