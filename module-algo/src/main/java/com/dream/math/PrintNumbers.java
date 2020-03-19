package com.dream.math;

/**
 * @author fanrui
 * @time 2020-03-19 22:23:47
 * 打印从1到最大的n位数
 * 剑指 Offer 17：https://leetcode-cn.com/problems/da-yin-cong-1dao-zui-da-de-nwei-shu-lcof/
 */
public class PrintNumbers {

    int[] res;
    int indexOfRes;

    // 递归方案
    public int[] printNumbers(int n) {
        int arrayLength = (int) Math.pow(10, n);
        res = new int[arrayLength - 1];
        indexOfRes = 0;
        char[] ch = new char[n];
        rec(ch, 0);
        return res;
    }

    // 递归，每一层设置其中 位的字符，且 从 0 遍历到 9
    private void rec(char[] ch, int index) {
        if (index == ch.length) {
            ch2Res(ch);
            return;
        }
        for (int i = 0; i < 10; i++) {
            ch[index] = (char) ('0' + i);
            rec(ch, index + 1);
        }
    }

    // 将结果保存到 res 数组中，需要将 0 过滤掉
    private void ch2Res(char[] ch) {
        int num = 0;
        for (int i = 0; i < ch.length; i++) {
            num = num * 10 + ch[i] - '0';
        }
        if (num == 0) {
            return;
        }
        res[indexOfRes++] = num;
    }

}
