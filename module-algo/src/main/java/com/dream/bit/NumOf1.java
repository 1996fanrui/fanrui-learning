package com.dream.bit;

/**
 * @author fanrui
 * 191. 位1的个数
 * LeetCode 191: https://leetcode-cn.com/problems/number-of-1-bits/
 * 统计一个 数转换成 二进制后， 1 的个数：
 * n = n & (n-1); 每运算一次，二进制少一个 1
 */
public class NumOf1 {

    public int hammingWeight(int n) {
        int count = 0;
        while(n != 0){
            n = n & (n-1);
            count++;
        }
        return count;
    }

}
