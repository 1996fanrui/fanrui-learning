package com.dream.base.linkedlist;

/**
 * @author fanrui
 * @time 2019-03-21 23:06:30
 * 检测链表是否有环
 * LeetCode 141: https://leetcode-cn.com/problems/linked-list-cycle/
 * 注意：快指针每次跳两步，一定要注意防止 空指针异常
 */
public class CheckCircle {


    /**
     * 检测链表是否有环
     *
     * @param head
     * @return desc:快指针，每次跳两个元素，慢指针每次跳一个元素。一直往后遍历，发现有尾节点，或者说发现next为null，则没有环
     */
    public boolean hasCycle(ListNode head) {

        if(head == null){
            return false;
        }

        ListNode fast = head.next;
        ListNode slow = head;

        while (fast != null){
            if(fast == slow){
                return true;
            }
            if(fast.next == null){
                return false;
            }
            fast = fast.next.next;
            slow = slow.next;
        }
        return false;
    }


    private static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }
}
