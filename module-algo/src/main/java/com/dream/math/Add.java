package com.dream.math;

/**
 * @author fanrui
 * @time 2020-04-05 13:24:36
 * 面试题65. 不用加减乘除做加法
 * 剑指 Offer 65： https://leetcode-cn.com/problems/bu-yong-jia-jian-cheng-chu-zuo-jia-fa-lcof/
 */
public class Add {
    public int add(int a, int b) {
        int sum;
        do {
            // 第一步：所有位进行累加
            sum = a ^ b;
            // 第二步，计算进位
            b = (a & b) << 1;
            a = sum;
        } while (b != 0); // 进位为 0，表示计算结束
        return sum;
    }
}
