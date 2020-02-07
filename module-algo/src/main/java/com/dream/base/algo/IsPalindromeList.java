package com.dream.base.algo;

/**
 * @author fanrui
 * 判断链表是否是回文串
 */
public class IsPalindromeList {

    public static class Node {
        public int value;
        public Node next;

        public Node(int data) {
            this.value = data;
        }
    }

    // need O(1) extra space
    public static boolean isPalindrome(Node head) {
        if (head == null || head.next == null) {
            return true;
        }
        Node n1 = head;
        Node n2 = head;
        // find mid node
        while (n2.next != null && n2.next.next != null) {
            n1 = n1.next; // n1 -> mid
            n2 = n2.next.next; // n2 -> end
        }

        n2 = n1.next; // n2 -> right part first node
        n1.next = null; // mid.next -> null

        // right part convert
        Node n3 = null;
        while (n2 != null) {
            n3 = n2.next; // n3 -> save next node
            n2.next = n1; // next of right node convert
            n1 = n2; // n1 move
            n2 = n3; // n2 move
        }

        n3 = n1; // n3 -> save last node
        n2 = head;// n2 -> left first node

        // check palindrome
        boolean res = true;
        while (n1 != null && n2 != null) {
            if (n1.value != n2.value) {
                res = false;
                break;
            }
            n1 = n1.next; // left to mid
            n2 = n2.next; // right to mid
        }

        n1 = n3.next;
        n3.next = null;
        while (n1 != null) { // recover list
            n2 = n1.next;
            n1.next = n3;
            n3 = n1;
            n1 = n2;
        }
        return res;
    }

}
