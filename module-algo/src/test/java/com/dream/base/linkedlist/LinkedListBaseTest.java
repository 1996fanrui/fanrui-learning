package com.dream.base.linkedlist;

import org.junit.Test;

public class LinkedListBaseTest {

    // 单链表反转
    @Test
    public void testReverse(){
        Node node = new Node(0,null);
        Node tail = node;
        for ( int i = 1 ; i< 5;i++ ){
            tail.next = new Node(i,null);
            tail = tail.next;
        }
        node.printAll();


        Reverse.reverse(node).printAll();
    }

    // 有序链表合并
    @Test
    public void testMergeSortedLists(){
        Node node1 = new Node(0,null);
        Node tail = node1;
        for ( int i = 1 ; i< 5;i++ ){
            tail.next = new Node(i,null);
            tail = tail.next;
        }
        node1.printAll();



        Node node2 = new Node(2,null);
        tail = node2;
        for ( int i = 10 ; i< 15;i++ ){
            tail.next = new Node(i,null);
            tail = tail.next;
        }
        node2.printAll();

        MergeSortedLists.mergeSortedLists(node1,node2).printAll();
    }

    // 检测链表是否有环
    @Test
    public void testCheckCircle(){


        Node node = new Node(0,null);
        Node tail = node;
        for ( int i = 1 ; i< 15;i++ ){
            tail.next = new Node(i,null);
            tail = tail.next;
        }

//        tail.next = node;

        System.out.println(CheckCircle.checkCircle(node));
    }

    // 求链表的中间结点
    @Test
    public void testFindMiddleNode(){


        Node node = new Node(0,null);
        Node tail = node;
        for ( int i = 1 ; i< 15;i++ ){
            tail.next = new Node(i,null);
            tail = tail.next;
        }

        FindMiddleNode.findMiddleNode(node).printAll();
    }

    // 删除链表倒数第 k 个结点
    @Test
    public void testDeleteLastKthNode(){


        Node node = new Node(0,null);
        Node tail = node;
        for ( int i = 1 ; i< 15;i++ ){
            tail.next = new Node(i,null);
            tail = tail.next;
        }

        DeleteLastKthNode.deleteLastKth(node,6).printAll();
    }




}
