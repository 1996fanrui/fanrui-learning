package com.dream.tree.unionfind;

import java.util.HashMap;
import java.util.List;

/**
 * 并查集的实现
 */
public class UnionFind {


    /**
     * HashMap 和 Node 方式实现并查集
     */
    public static class UnionFindSetByLink {

        public static class Node {
            // whatever you like
        }

        /**
         * fatherMap 存储当前节点与 parent 的映射关系
         * key 为当前节点，value 为 parent 节点
          */
        public HashMap<Node, Node> fatherMap;

        /**
         * sizeMap 存储着当前集合存储的元素个数，
         * 每次检查代表节点对应的 value 即可
         */
        public HashMap<Node, Integer> sizeMap;

        public UnionFindSetByLink() {
            fatherMap = new HashMap<>();
            sizeMap = new HashMap<>();
        }

        /**
         * 将一个 list 初始化成并查集，每个元素自己是一个集合
         * @param nodes
         */
        public void makeSets(List<Node> nodes) {
            fatherMap.clear();
            sizeMap.clear();
            for (Node node : nodes) {
                // 初始化时，所有 parent 指向自己，所有集合元素数设置为 1
                fatherMap.put(node, node);
                sizeMap.put(node, 1);
            }
        }

        /**
         * 查找 node 的代表节点，并压缩路径
         * @param node
         * @return
         */
        private Node findHead(Node node) {
            // 往 parent 方向找
            Node father = fatherMap.get(node);
            if (father != node) {
                // 通过递归，找到代表节点
                father = findHead(father);
            }
            // 将当前节点指向 代表节点，
            // 递归的过程中，会将 node 到代表节点中间的所有节点 都指向代表节点
            fatherMap.put(node, father);
            return father;
        }

        /**
         * 两个 node，是否是一个集合，
         * 只需要判断两个 代表节点是否相同即可
         * @param a
         * @param b
         * @return
         */
        public boolean isSameSet(Node a, Node b) {
            return findHead(a) == findHead(b);
        }

        /**
         * 合并两个集合
         * @param a
         * @param b
         */
        public void union(Node a, Node b) {
            if (a == null || b == null) {
                return;
            }
            Node aHead = findHead(a);
            Node bHead = findHead(b);
            // 两个节点是一个集合，直接 return
            if (aHead == bHead) {
                return;
            }

            // 比较两个集合，将元素较少的集合 合并到元素较多的集合
            int aSetSize= sizeMap.get(aHead);
            int bSetSize = sizeMap.get(bHead);
            if (aSetSize <= bSetSize) {
                fatherMap.put(aHead, bHead);
                sizeMap.put(bHead, aSetSize + bSetSize);
            } else {
                fatherMap.put(bHead, aHead);
                sizeMap.put(aHead, aSetSize + bSetSize);
            }
        }

    }


}
