package com.dream.bit;

/**
 * @author fanrui
 * LeetCode 461：https://leetcode-cn.com/problems/hamming-distance/
 * 两个整数之间的汉明距离指的是这两个数字对应二进制位不同的位置的数目。
 * 思路：既然是不同的位置上的数目，那就使用异或，不同为1，最后求 1 的个数即可，也就是求异或后的汉明重量
 */
public class HammingDistance {
    public int hammingDistance(int x, int y) {
        int z = x ^ y;
        int res = 0;
        while(z!=0){
            z = z & (z-1);
            res++;
        }
        return res;
    }
}
