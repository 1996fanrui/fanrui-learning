package com.dream.math;

/**
 * @author fanrui
 * @time 2020-03-21 17:35:16
 * 数字序列中某一位的数字
 * LeetCode 400: https://leetcode-cn.com/problems/nth-digit/
 * 剑指 Offer 44：https://leetcode-cn.com/problems/shu-zi-xu-lie-zhong-mou-yi-wei-de-shu-zi-lcof/
 */
public class NthDigit {

    public int findNthDigit(int n) {
        if (n <= 9) {
            return n;
        }

        // 所要找的数字的位数
        int numLength = 2;
        // 前一个区间数字填满后，当前的位数
        long length = 9;
        while (true) {
            // 当前长度的数字填满后，需要占用的位数。例如 长度为 2 的数组有 90 个。
            long curLengthCount = 9L * (long) Math.pow(10, numLength - 1) * numLength;
            if (length + curLengthCount >= n) {
                break;
            }
            // 否则，位数增加
            numLength++;
            length += curLengthCount;
        }

        // rank 表示处于当前区间的第几个数
        int rank = (int)((n - length - 1) / numLength);
        // 第 n 个位置对应的数字就是 num
        int num = (int) Math.pow(10, numLength - 1) + rank;
        // 第 n 个位置 对应 num 数字的第几位(从左数，第一位是 0)
        int bitOfnum = (int)(n - length - 1) % numLength;
        // 第 n 个位置 对应 num 数字的第几位(从右数，第一位是 0)
        bitOfnum = numLength - bitOfnum -1;


        for (int i = 0; i < bitOfnum; i++) {
            num /= 10;
        }
        return num % 10;
    }

}
