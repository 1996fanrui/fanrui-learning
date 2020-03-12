package com.dream.base.linkedlist;

/**
 * @author fanrui
 * K 个一组翻转链表
 * 如果 k = 2，题目就变成了两两交换链表中的节点
 * LeetCode 25：https://leetcode-cn.com/problems/reverse-nodes-in-k-group/
 */
public class ReverseKGroup {

    // test case:
    // [1,2,3,4,5]
    // 2

    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode cur = new ListNode(0);
        cur.next = head;
        head = cur;
        // 返回下一组的 尾节点
        ListNode nextGroupEnd = getNextGroupEnd(cur, k);

        // 存在尾节点，说明 下一个组是完整的，需要调整顺序，否则整个任务结束
        while (nextGroupEnd != null){
            ListNode nextGroupStart = cur.next;
            // 下下个分组的 头节点
            ListNode nextNextNode = nextGroupEnd.next;
            // 下一个分组与 后续分组断开
            nextGroupEnd.next = null;
            // 下一个分组 反转，放到 cur.next
            cur.next = reverse(nextGroupStart);
            // nextGroupStart 已经变成了 下一个组的 end，指向下下个分组
            nextGroupStart.next = nextNextNode;
            nextGroupEnd = getNextGroupEnd(nextGroupStart, k);
            cur = nextGroupStart;
        }

        return head.next;
    }

    // 反转单链表
    private static ListNode reverse(ListNode cur){
        ListNode oldNode = cur;
        ListNode newNode = null;

        while (oldNode != null){
            ListNode nextNode = oldNode.next;
            oldNode.next = newNode;
            newNode = oldNode;
            oldNode = nextNode;
        }
        return newNode;
    }

    // 返回下一组的 尾节点，存在尾节点，说明 下一个组是完整的
    // 如果下一个组不完整，返回 null
    private static ListNode getNextGroupEnd(ListNode cur, int k) {
        ListNode res = cur;
        for (int i = 0; i < k; i++) {
            if (res == null) {
                return null;
            }
            res = res.next;
        }
        return res;
    }


    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

}
