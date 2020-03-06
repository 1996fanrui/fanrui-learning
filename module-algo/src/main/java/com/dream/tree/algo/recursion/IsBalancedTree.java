package com.dream.tree.algo.recursion;

import scala.Tuple2;

/**
 * @author fanrui
 * 判断一棵树，是否是平衡树
 */
public class IsBalancedTree {


    public static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int data) {
            this.value = data;
        }
    }

    // 可以优化这个返回值，不需要 boolean 类型，当 高度返回 < 0 时就认为是不平衡的
    public static Tuple2<Boolean, Integer> isBalancedTree(Node node){
        if(node == null){
            return Tuple2.apply(true, 0);
        }

        Tuple2<Boolean, Integer> leftResult = isBalancedTree(node.left);
        // 一旦返回 false，已经不关注树的高度了
        if(!leftResult._1 ){
            return Tuple2.apply(false,0);
        }

        Tuple2<Boolean, Integer> rightResult = isBalancedTree(node.right);
        // 一旦返回 false，已经不关注树的高度了
        if(!rightResult._1){
            return Tuple2.apply(false,0);
        }

        // 两颗树的高度差值 > 1 返回 false
        if(Math.abs(leftResult._2 - rightResult._2) > 1){
            return Tuple2.apply(false,0);
        } else {
            return Tuple2.apply(true, Math.max(leftResult._2, rightResult._2) + 1);
        }
    }
}
