package com.dream.tree.unionfind;


/**
 * @author fanrui
 * 数组方式实现并查集
 */
public class UnionFindSetByArray {

    /**
     * 使用一个数组 parent，第 i 位上的元素存储着 j，表示 i 的 parent 是 j
     * 初始化时，每一位都存储自己的下标，即：第 i 位上的元素存储着 i，第 j 位上的元素存储着 j。
     * 来表示每一位都指向自己
     */
    private int[] parent;

    /**
     * rank 数组用来保存集合的高度，第 i 位上元素存储着 j，
     * 表示以 i 为代表节点的集合高度为 j
     */
    int[] rank;

    /**
     * 初始化一个容量为 N 的并查集，每个元素指向自己
     */
    public UnionFindSetByArray(int N){
        parent = new int[N];
        rank = new int[N];
        for(int i = 0; i < N; i++){
            // parent，第 i 位上的元素存储着 i，表示第 i 位指向自己
            parent[i] = i;
            // 初始化时，集合的初始高度都为 0
            rank[i] = 0;
        }
    }


    /**
     * 查找节点 i 的代表节点
     * @param i
     * @return
     */
    private int findHead(int i) {
        int head = parent[i];
        // 一直往 parent 方向去找，直到 parent 指向自己，就找到了 代表节点
        while (head != parent[head]) {
            head = parent[head];
        }

        // 将该路径上所有元素直接指向代表节点
        while (i != head) {
            int tmp = parent[i];
            // 将 i 节点的 parent 指向 head 节点
            parent[i] = head;
            i = tmp;
        }
        return head;
    }


    /**
     * 递归法，直接搞定查找 + 压缩路径
     * @param i
     * @return
     */
    private int findHead1(int i) {
        if(i != parent[i]){
            parent[i] = findHead(parent[i]);
        }
        return parent[i];
    }


    /**
     * 判断节点 i 、 j 是否是同一个集合
     * @param i
     * @param j
     * @return
     */
    public boolean isSameSet(int i, int j) {
        return findHead(i) == findHead(j);
    }


    /**
     * 合并集合 i 、j
     * @param i
     * @param j
     */
    public void union(int i, int j) {
        int iHead = findHead(i);
        int jHead = findHead(j);
        // 两个元素是一个集合，直接返回
        if(iHead == jHead){
            return;
        }

        // 元素数量少的集合 指向元素数量大的集合，并更新 rank
        if(rank[iHead] < rank[jHead]){
            parent[iHead] = jHead;
        } else if (rank[iHead] > rank[jHead]){
            parent[jHead] = iHead;
        } else {
            parent[iHead] = jHead;
            rank[jHead] += 1;
        }
    }

}
