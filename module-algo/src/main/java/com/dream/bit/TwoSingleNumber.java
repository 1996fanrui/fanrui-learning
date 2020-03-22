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

        // 只保留 allXor 最低位的 1 到 diff
        int diff = allXor & (-allXor);

        int num1 = 0;
        for (int i = 0; i < nums.length; i++) {
            // 第 lastIndexOfBit1 位为 1 的数据
            if((diff & nums[i]) == 0){
                num1 ^= nums[i];
            }
//           num2 有更高效的求法，所以这里注释
//            else {
//                num2 ^= nums[i];
//            }
        }

        // 异或满足交换律，allXor = num2 ^ num1：所以， num2 = allXor ^ num1
        int num2 = allXor ^ num1;
        return new int[]{num1,num2};
    }


}
