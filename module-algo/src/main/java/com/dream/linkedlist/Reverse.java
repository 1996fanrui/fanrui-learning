package com.dream.linkedlist;

/**
 * 单链表反转
 * @author fanrui
 * @time 2019-03-21 17:03:47
 * @desc 创建两个链表，一个原链表，一个新链表，遍历原链表，将原链表的节点一个个添加到新链表的头部即可
 */
public class Reverse {

    /**
     * @desc 单链表反转
     * @param list 要反转的链表
     * @return 反转后的链表
     */
    public static Node reverse(Node list) {
        // 新链表的头结点
        Node newNode = null;
        // 旧链表的头节点
        Node oldNode = list;

        while (oldNode != null) {
            //暂存 旧链表的后继节点
            Node nextNode = oldNode.next;
            //旧链表的头结点移到新链表中
            oldNode.next = newNode;
            //新链表更新头节点
            newNode = oldNode;
            //旧链表更新头结点
            oldNode = nextNode;
        }

        return newNode;
    }

}
