package com.dream.tree.unionfind.Island;


import java.util.HashMap;
import java.util.Map;

public class UnionFindSetByLink{

    static HashMap<Integer, Node> map;

    private static class Node{
        // 当前节点 parent 的 index，
        // 这个 index 是由二维数组转换为一维的 index
        int parent;

        // 当前集合的 size
        int size;

        public Node(int parent, int size) {
            this.parent = parent;
            this.size = size;
        }

    }
    public static int numIslands(char[][] grid) {
        map = new HashMap<>();
        if (grid == null || grid.length == 0 || grid[0] == null) {
            return 0;
        }
        int N = grid.length;
        int M = grid[0].length;
        int res = 0;
        // 遍历所有元素
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                // 发现是 1， 将该节点假如到 map，节点指向自己本身，且 size = 1
                if (grid[i][j] == 1) {
                    map.put(i*M + j, new Node(i*M + j, 1));
                    res++;

                    // 左边如果为 1，与左节点 union
                    if(j > 0 && grid[i][j-1] == 1){
                        res += union(i*M + j -1,i*M + j);
                    }

                    // 上边如果为 1，与上节点 union
                    if(i > 0 && grid[i-1][j] == 1){
                        res += union((i-1)*M + j,i*M + j);
                    }
                }
            }
        }
        return res;

    }


    private static int findHead(int i) {
        while (i != map.get(i).parent){
            i = map.get(i).parent;
        }
        return i;
    }


    private static int union(int i, int j) {
        int iHead = findHead(i);
        int jHead = findHead(j);
        if(iHead == jHead){
            return 0;
        }

        Node iNode = map.get(iHead);
        Node jNode = map.get(jHead);
        if(iNode.size < map.get(jHead).size) {
            iNode.parent = j;
            jNode.size += iNode.size;
        } else {
            jNode.parent = i;
            iNode.size += jNode.size;
        }
        return -1;
    }

    private static void printMap(){
        for(Map.Entry<Integer,Node> entry: map.entrySet()){
            if(entry.getKey() == entry.getValue().parent){
                System.out.println("代表节点"+ entry.getKey() +" 对应的集合元素为" + entry.getValue().size);
            }
        }
    }


    public static void main(String[] args) {
        char[][] m1 = {  { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 1, 0, 1, 1, 1, 0 },
                { 0, 1, 1, 1, 0, 0, 0, 1, 0 },
                { 0, 1, 1, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };
        System.out.println(numIslands(m1));

        char[][] m2 = {  { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
                { 0, 1, 1, 1, 0, 0, 0, 1, 0 },
                { 0, 1, 1, 0, 0, 0, 1, 1, 0 },
                { 0, 0, 0, 0, 0, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };
        System.out.println(numIslands(m2));
        char[][] m3 = {  { 1, 1, 1, 1, 0 },
                { 1, 1, 0, 1, 0 },
                { 1, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 }, };
        System.out.println(numIslands(m3));
        char[][] m4 = {};
        System.out.println(numIslands(m4));

    }


}
