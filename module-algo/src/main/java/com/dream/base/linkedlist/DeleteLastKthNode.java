package com.dream.base.linkedlist;

/**
 * @author fanrui
 * @time 2019-03-21 23:06:38
 */
public class DeleteLastKthNode {

    /**
     * 删除链表倒数第 k 个结点
     * @param list
     * @param k
     * @return
     * @desc 两个前后指针，前指针先遍历n个元素，后指针在头结点，
     * 然后两个指针同时往后开始遍历，前指针到了链表尾，后指针就是第n个节点了，然后删掉即可
     */
    public static Node deleteLastKth(Node list, int k) {
        Node fast = list;
        int i = 1;
        while (fast != null && i < k) {
            fast = fast.next;
            ++i;
        }

        if (fast == null) {
            return list;
        }

        Node slow = list;
        Node prev = null;
        while (fast.next != null) {
            fast = fast.next;
            prev = slow;
            slow = slow.next;
        }

        if (prev == null) {
            list = list.next;
        } else {
            prev.next = prev.next.next;
        }
        return list;
    }
}
