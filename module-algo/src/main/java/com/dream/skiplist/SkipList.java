package com.dream.skiplist;


import java.util.Random;

/**
 * 跳表的一种实现方法。
 * 跳表中存储的是正整数，并且存储的是不重复的。
 *
 * @author geek_algo ZHENG
 */
public class SkipList {

    private static final int MAX_LEVEL = 16;

    private int levelCount = 1;

    // 带头链表
    private Node head = new Node();

    private Random r = new Random();

    public Node find(int value) {
        Node p = head;
        for (int i = levelCount - 1; i >= 0; --i) {
            while (p.forwards[i] != null && p.forwards[i].data < value) {
                p = p.forwards[i];
            }
        }

        if (p.forwards[0] != null && p.forwards[0].data == value) {
            return p.forwards[0];
        } else {
            return null;
        }
    }

    public void insert(int value) {
        int level = randomLevel();
        Node newNode = new Node();
        newNode.data = value;
        newNode.maxLevel = level;
        Node[] update = new Node[level];
        for (int i = 0; i < level; ++i) {
            update[i] = head;
        }

        // record every level largest value which smaller than insert value in update[]
        // 这里的p只有刚开始初始化一次，不用每层都从头节点开始找要插入的位置，每层从上一层找到的p接着往后找就行了
        Node p = head;
        for (int i = level - 1; i >= 0; --i) {
            // 第i层p元素的后续节点不为空，且 后续节点的值小于要插入的值，则p指向后续节点
            while (p.forwards[i] != null && p.forwards[i].data < value) {
                // 第i层p元素指向下一个节点
                p = p.forwards[i];
            }
            // 新加入的节点在第i层，就应该放置到 经过上述条件找到的p点 之后
            // use update save node in search path
            update[i] = p;
        }
        //update[i] 表示新加入的节点在第i层，应该放到 update[i] 之后

        // in search path node next node become new node forwords(next)
        for (int i = 0; i < level; ++i) {
            // 这里就是一个 链表中 增加元素的操作
            newNode.forwards[i] = update[i].forwards[i];
            update[i].forwards[i] = newNode;
        }

        // update node hight
        if (levelCount < level) {
            levelCount = level;
        }
    }

    public void delete(int value) {
        Node[] update = new Node[levelCount];
        Node p = head;
        for (int i = levelCount - 1; i >= 0; --i) {
            while (p.forwards[i] != null && p.forwards[i].data < value) {
                p = p.forwards[i];
            }
            // 与插入类似，每次找到每层中小于 value 的最大值对应的节点
            update[i] = p;
        }

        // 最下层 有对应的value节点
        if (p.forwards[0] != null && p.forwards[0].data == value) {
            // 从上层 往最下层遍历，遍历所有层
            for (int i = levelCount - 1; i >= 0; --i) {
                // 第i层有对应的value节点
                if (update[i].forwards[i] != null && update[i].forwards[i].data == value) {
                    update[i].forwards[i] = update[i].forwards[i].forwards[i];
                }
            }
            // todo 优化点，可以从下层往上层遍历，当发现某曾没有对应节点时，他的上层肯定没有对应节点，停止遍历吧
        }
    }

    /**
     * 随机 level 次，如果是奇数层数 +1，防止伪随机
      */
    private int randomLevel() {
        int level = 1;
        for (int i = 1; i < MAX_LEVEL; ++i) {
            if (r.nextInt() % 2 == 1) {
                level++;
            }
        }

        return level;
    }

    public void printAll() {
        Node p = head;
        while (p.forwards[0] != null) {
            System.out.print(p.forwards[0] + " ");
            p = p.forwards[0];
        }
        System.out.println();
    }

    public class Node {
        public int data = -1;

        /**
         *  每个节点有 MAX_LEVEL 个后续节点，其中 forwards[i] 表示第i层索引的后续节点
         *  第0层存储着所有节点
          */
        public Node[] forwards = new Node[MAX_LEVEL];
        public int maxLevel = 0;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{ data: ");
            builder.append(data);
            builder.append("; levels: ");
            builder.append(maxLevel);
            builder.append(" }");

            return builder.toString();
        }
    }

}
