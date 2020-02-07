package com.dream.base.linkedlist;

/**
 * @author fanrui
 * @time 2019-03-21 23:06:30
 */
public class CheckCircle {

    /**
     * 检测链表是否有环
     * @param list
     * @return
     * desc:快指针，每次跳两个元素，慢指针每次跳一个元素。一直往后遍历，发现有尾节点，或者说发现next为null，则没有环
     */
    public static boolean checkCircle(Node list) {
        if (list == null) {
            return false;
        }

        Node fast = list.next;
        Node slow = list;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;

            if (slow == fast) {
                return true;
            }
        }

        return false;
    }

}
