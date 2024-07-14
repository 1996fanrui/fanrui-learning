package com.dream.base.linkedlist;

/**
 * implement by zuochengyun
 * 约瑟夫问题
 */
public class JosephusProblem {


    public static class Node {
        public int value;
        public Node next;

        public Node(int data) {
            this.value = data;
        }
    }

    // 一圈一圈的杀节点，循环 m 次杀一个节点
    public static Node josephusKill1(Node head, int m) {
        if (head == null || head.next == head || m < 1) {
            return head;
        }
        // last 永远指向 head 的上一个节点
        Node last = head;
        while (last.next != head) {
            last = last.next;
        }
        int count = 0;
        while (head != last) {
            if (++count == m) {
                // 要杀节点了，也就是链表中要剔除节点了。
                // 当前的 head 要被剔除了，也就是 last 的 next 直接指向 head 的 next。
                // 本次循环，last 保持不变，head 往后跳了一个。
                last.next = head.next;
                count = 0;
            } else {
                // 正常情况，不杀节点，所以 last 往后跳一个
                last = last.next;
            }
            // head 永远是 last 的 next
            head = last.next;
        }
        return head;
    }

    // 使用 公式法直接计算出谁应该留下
    public static Node josephusKill2(Node head, int m) {
        if (head == null || head.next == head || m < 1) {
            return head;
        }
        Node cur = head.next;
        int tmp = 1; // tmp -> list size
        while (cur != head) {
            tmp++;
            cur = cur.next;
        }
        tmp = getLive(tmp, m); // tmp -> service node position
        while (--tmp != 0) {
            head = head.next;
        }
        head.next = head;
        return head;
    }

    public static int getLive(int i, int m) {
        if (i == 1) {
            return 1;
        }
        return (getLive(i - 1, m) + m - 1) % i + 1;
    }

    // leet code 上只给了 n
    // LeetCode ：https://leetcode-cn.com/problems/yuan-quan-zhong-zui-hou-sheng-xia-de-shu-zi-lcof/
    public int lastRemaining(int n, int m) {
        if(n == 1){
            return 0;
        }
        int x = lastRemaining(n-1, m);
        return (m + x) % n;
    }

    public static void printCircularList(Node head) {
        if (head == null) {
            return;
        }
        System.out.print("Circular List: " + head.value + " ");
        Node cur = head.next;
        while (cur != head) {
            System.out.print(cur.value + " ");
            cur = cur.next;
        }
        System.out.println("-> " + head.value);
    }

    public static void main(String[] args) {
        Node head1 = new Node(1);
        head1.next = new Node(2);
        head1.next.next = new Node(3);
        head1.next.next.next = new Node(4);
        head1.next.next.next.next = new Node(5);
        head1.next.next.next.next.next = head1;
        printCircularList(head1);
        head1 = josephusKill1(head1, 3);
        printCircularList(head1);

        Node head2 = new Node(1);
        head2.next = new Node(2);
        head2.next.next = new Node(3);
        head2.next.next.next = new Node(4);
        head2.next.next.next.next = new Node(5);
        head2.next.next.next.next.next = head2;
        printCircularList(head2);
        head2 = josephusKill2(head2, 3);
        printCircularList(head2);

    }

}
