package com.dream.base.linkedlist;

import java.util.HashMap;

/**
 * @author fanrui
 * 138. 复制带随机指针的链表
 * LeetCode 138：https://leetcode-cn.com/problems/copy-list-with-random-pointer/
 * 剑指 Offer 35： https://leetcode-cn.com/problems/fu-za-lian-biao-de-fu-zhi-lcof/
 */
public class CopyRandomList {

    private static class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    // 方案一： 使用 HashMap 维护老节点到新节点的映射关系
    private static HashMap<Node,Node> map = new HashMap<>();

    public Node copyRandomList1(Node head) {
        map.clear();
        if(head == null){
            return null;
        }

        // 创建所有节点的副本到放到 map 中
        Node cur = head;
        Node curCopy;
        while (cur != null){
            curCopy = new Node(cur.val);
            map.put(cur,curCopy);
            cur = cur.next;
        }


        // 再次遍历，对副本的 next 指针和 random 指针进行赋值，赋值必须为 副本的 Node
        cur = head;
        while (cur != null){
            curCopy = map.get(cur);
            curCopy.next = map.get(cur.next);
            if(cur.random != null){
                curCopy.random = map.get(cur.random);
            }
            cur = cur.next;
        }
        return map.get(head);
    }


    // 方案二： 将新节点串在老节点中间，从而维护了老节点到新节点的映射关系
    public Node copyRandomList(Node head) {
        Node cur = head;
        // new 出所有的新节点，串在 老节点后面
        while (cur != null){
            Node next = cur.next;
            cur.next = new Node(cur.val);
            cur = cur.next;
            cur.next = next;
            cur = cur.next;
        }

        // 串联 random 指针
        cur = head;
        while (cur != null){
            Node newNode = cur.next;
            // 重点：当前的新节点 random 指针就是当前节点 random 指针的 next
            if(cur.random != null){
                newNode.random = cur.random.next;
            }
            // cur 后移两位
            cur = cur.next.next;
        }

        // 从链表中 提取出结果
        cur = head;
        // 搞个哨兵，便于编码
        Node res = new Node(0);
        Node resTail = res;
        while (cur != null){
            // 结果链表中串上新节点
            Node newNode = cur.next;
            resTail.next = newNode;
            resTail = newNode;
            // 还原旧链表
            cur.next = newNode.next;
            cur = cur.next;
        }
        return res.next;
    }



}
