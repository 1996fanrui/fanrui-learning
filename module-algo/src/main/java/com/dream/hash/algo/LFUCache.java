package com.dream.hash.algo;

import java.util.HashMap;

/**
 * implement by zuochengyun
 * LeetCode 链接：https://leetcode-cn.com/problems/lfu-cache/
 */
public class LFUCache {

    public static class Node {
        public Integer key;
        public Integer value;
        public Integer times;
        public Node up;
        public Node down;

        public Node(int key, int value, int times) {
            this.key = key;
            this.value = value;
            this.times = times;
        }
    }

    public static class NodeList {
        public Node head;
        public Node tail;
        public NodeList last;
        public NodeList next;

        public NodeList(Node node) {
            head = node;
            tail = node;
        }

        public void addNodeFromHead(Node newHead) {
            newHead.down = head;
            head.up = newHead;
            head = newHead;
        }

        public boolean isEmpty() {
            return head == null;
        }

        public void deleteNode(Node node) {
            if (head == tail) {
                head = null;
                tail = null;
            } else {
                if (node == head) {
                    head = node.down;
                    head.up = null;
                } else if (node == tail) {
                    tail = node.up;
                    tail.down = null;
                } else {
                    node.up.down = node.down;
                    node.down.up = node.up;
                }
            }
            node.up = null;
            node.down = null;
        }
    }

    private int capacity;
    private int size;
    private HashMap<Integer, Node> records;
    private HashMap<Node, NodeList> heads;
    private NodeList headList;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.records = new HashMap<>();
        this.heads = new HashMap<>();
        headList = null;
    }

    public void put(int key, int value) {
        if (records.containsKey(key)) {
            Node node = records.get(key);
            node.value = value;
            node.times++;
            NodeList curNodeList = heads.get(node);
            move(node, curNodeList);
        } else {
            if (size == capacity) {
                Node node = headList.tail;
                headList.deleteNode(node);
                modifyHeadList(headList);
                records.remove(node.key);
                heads.remove(node);
                size--;
            }
            Node node = new Node(key, value, 1);
            if (headList == null) {
                headList = new NodeList(node);
            } else {
                if (headList.head.times.equals(node.times)) {
                    headList.addNodeFromHead(node);
                } else {
                    NodeList newList = new NodeList(node);
                    newList.next = headList;
                    headList.last = newList;
                    headList = newList;
                }
            }
            records.put(key, node);
            heads.put(node, headList);
            size++;
        }
    }

    // 将指定 node 移动到频次 + 1  的 NodeList 中
    private void move(Node node, NodeList oldNodeList) {
        oldNodeList.deleteNode(node);
        NodeList preList = modifyHeadList(oldNodeList) ? oldNodeList.last
                : oldNodeList;
        NodeList nextList = oldNodeList.next;
        if (nextList == null) {
            NodeList newList = new NodeList(node);
            if (preList != null) {
                preList.next = newList;
            }
            newList.last = preList;
            if (headList == null) {
                headList = newList;
            }
            heads.put(node, newList);
        } else {
            if (nextList.head.times.equals(node.times)) {
                nextList.addNodeFromHead(node);
                heads.put(node, nextList);
            } else {
                NodeList newList = new NodeList(node);
                if (preList != null) {
                    preList.next = newList;
                }
                newList.last = preList;
                newList.next = nextList;
                nextList.last = newList;
                if (headList == nextList) {
                    headList = newList;
                }
                heads.put(node, newList);
            }
        }
    }

    // 检查是否删除 指定的 NodeList
    private boolean modifyHeadList(NodeList nodeList) {
        if (nodeList.isEmpty()) {
            if (headList == nodeList) {
                headList = nodeList.next;
                if (headList != null) {
                    headList.last = null;
                }
            } else {
                nodeList.last.next = nodeList.next;
                if (nodeList.next != null) {
                    nodeList.next.last = nodeList.last;
                }
            }
            return true;
        }
        return false;
    }

    public int get(int key) {
        if (!records.containsKey(key)) {
            return -1;
        }
        Node node = records.get(key);
        node.times++;
        NodeList curNodeList = heads.get(node);
        move(node, curNodeList);
        return node.value;
    }

}
