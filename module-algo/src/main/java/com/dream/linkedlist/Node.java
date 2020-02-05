package com.dream.linkedlist;

/**
 * 链表的节点
 * @author fanrui
 * @time 2019-03-21 17:04:18
 */
public class Node {
    public int data;
    public Node next;

    public Node(int data, Node next) {
        this.data = data;
        this.next = next;
    }

    public int getData() {
        return data;
    }


    public void printAll() {
        Node p = this;
        System.out.print("[");
        while (p != null) {
            System.out.print(p.data + ",");
            p = p.next;
        }
        System.out.println("]");
    }
}
