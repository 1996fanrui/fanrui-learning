package com.dream.hash.algo;

import java.util.HashMap;

/**
 *
 * @author fanrui
 * @time 2020-03-07 10:13:00
 *
 * LRUCache
 * LeetCode：https://leetcode-cn.com/problems/lru-cache/
 *          https://leetcode-cn.com/problems/lru-cache-lcci/
 *          两个题目一样
 *
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
class LRUCache {

    HashMap<Integer, Node> map;
    DoubleLinkedList list;
    int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap(capacity + capacity>>1);
        list = new DoubleLinkedList();
    }


    public int get(int key) {
        Node node = map.get(key);
        if(node == null){
            return -1;
        }
        list.moveNode2Tail(node);
        return node.value;
    }


    public void put(int key, int value) {
        // 新加的 key 是个新数据
        if(!map.containsKey(key)){
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            list.addNewNode(newNode);
        } else {
            // 新加的 key 在 map 中存在
            Node node = map.get(key);
            node.value = value;
            list.moveNode2Tail(node);
        }

        // Cache 容量超了
        if(map.size() > capacity) {
            Node oldestNode = list.removeOldestNode();
            map.remove(oldestNode.key);
        }
    }


    private static class Node{
        public int key;
        public int value;
        Node pre;
        Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }



    private static class DoubleLinkedList{

        /**
         * head：头部表示最旧的数据
         * tail：尾部表示最新的数据
         */
        private Node head;
        private Node tail;


        /**
         *
         * @param node
         */
        public void moveNode2Tail(Node node) {
            // node 当前就是尾部
            if(tail == node){
                return;
            }

            // 链表中没有元素
            if(tail == null){
                head = tail = node;
            }

            tail.next = node;

            // node 是头部
            if(node == head){
                node.pre = tail;
                head = head.next;
                node.next = null;
                head.pre = null;
                tail = node;
                return;
            }

            // node 非头部，非尾部
            if(node.next != null){
                node.next.pre = node.pre;
                node.pre.next = node.next;
                node.pre = tail;
                node.next = null;
                tail = node;
            }

        }


        /**
         * 链表中移除最老的数据，并返回删除的 node
         */
        public Node removeOldestNode(){
            // 链表中没有元素，返回 null
            if(head == null){
                return null;
            } else if(head == tail) {
                // 仅剩一个元素了
                head = tail = null;
                return head;
            }
            // 剩余多个元素的情况
            Node res = head;
            head = head.next;
            head.pre = null;
            res.next = null;
            return res;
        }


        /**
         * 添加一个 新的 node 到链表尾部
         * @param newNode
         */
        public void addNewNode(Node newNode) {
            if(tail == null) {
                head = tail = newNode;
                return;
            }
            tail.next = newNode;
            newNode.pre = tail;
            tail = newNode;
        }
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(2, 1);
        cache.put(1, 1);
        cache.put(2, 3);
        cache.put(4, 1);
        System.out.println(cache.get(1));       // 返回 -1
        System.out.println(cache.get(2));       // 返回 3
    }


}
