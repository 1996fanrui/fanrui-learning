package com.dream.base.linkedlist;

/**
 * @author fanrui
 * @time 2019-03-21 23:06:44
 */
public class FindMiddleNode {

    /**
     *  求链表的中间结点
     * @param list
     * @return
     * @desc 两个指针a,b初始时都指向头指针，a指针每次往后移动一个元素，b指针每次往后移动两个元素，
     *          当b指针指向链表尾时，a指针就指向了链表的中间节点
     */
    public static Node findMiddleNode(Node list) {
        if (list == null) {
            return null;
        }

        Node fast = list;
        Node slow = list;

        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }

        return slow;
    }
}
