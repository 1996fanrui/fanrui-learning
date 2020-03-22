package com.dream.bit;

/**
 * @author fanrui
 * @time 2020-03-22 11:52:38
 * 找出两个只出现一次的数字（其他数字出现了两次）
 * 剑指 Offer 56-I：https://leetcode-cn.com/problems/shu-zu-zhong-shu-zi-chu-xian-de-ci-shu-lcof/
 * LeetCode 260：https://leetcode-cn.com/problems/single-number-iii/
 */
public class TwoSingleNumber {

    // 思路：数组中所有数进行异或操作，自己跟自己异或为 0，所以异或的最后结果就是结果
    public int[] singleNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new int[0];
        }

        int allXor = 0;
        for (int i = 0; i < nums.length; i++) {
            allXor ^= nums[i];
        }

        int lastIndexOfBit1 = findLastIndexOfBit1(allXor);

        // 1 左移 lastIndexOfBit1 就可以就可以用来分类了
        int diff = 1 << lastIndexOfBit1;

        int num1 = 0;
        int num2 = 0;
        for (int i = 0; i < nums.length; i++) {
            // 第 lastIndexOfBit1 位为 1 的数据
            if((diff & nums[i]) == 0){
                num1 ^= nums[i];
            } else {
                // 第 lastIndexOfBit1 位为 0 的数据
                num2 ^= nums[i];
            }
        }

        return new int[]{num1,num2};
    }


    // 找出二进制位 中最后一个 1 的位置
    private int findLastIndexOfBit1(int num) {
        int index = 0;
        while ((num & 1) != 1) {
            num >>= 1;
            index++;
        }
        return index;
    }

}
