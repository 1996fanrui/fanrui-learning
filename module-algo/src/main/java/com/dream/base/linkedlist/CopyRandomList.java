package com.dream.base.linkedlist;

import java.util.HashMap;

/**
 * @author fanrui
 * 138. 复制带随机指针的链表
 * LeetCode 138：https://leetcode-cn.com/problems/copy-list-with-random-pointer/
 *
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

    private static HashMap<Node,Node> map = new HashMap<>();

    public Node copyRandomList(Node head) {
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


}
