package com.dream.bit;

/**
 * @author fanrui
 * 找出只出现一次的数字（其他数字出现了三次）
 * LeetCode 137: https://leetcode-cn.com/problems/single-number-ii/
 * 剑指 Offer 56-II：https://leetcode-cn.com/problems/shu-zu-zhong-shu-zi-chu-xian-de-ci-shu-ii-lcof/
 * 思路：
 * 如果一个数字出现了 3 次，那么他们的二进制表示的每一位（0 或 1）也出现了 3 次。
 * 如果把所有出现了 3 次的数字的二进制的每一位都加起来，那么这一位的和能被 3 整除。
 * 把数组中所有数字的二进制表示的每一位累加起来。
 * 如果某一位能被 3 整除，那么那个只出现一次的数字二进制表示中对应的那一位就是 0，否则就是 1。
 */
public class SingleNumberOtherThree {

    public int singleNumber(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0];
        }

        // 搞一个 32 位的计数器，
        // count[0] 表示所有数的 最高位 二进制累加和
        // count[1] 表示所有数的 次高位 二进制累加和
        // 。。。
        // count[31] 表示所有数的 最低位 二进制累加和
        int[] count = new int[32];

        for (int i = 0; i < nums.length; i++) {
            int bit = 1;
            for (int j = 31; j >= 0; j--) {
                // nums[i] j 位置为 1
                if ((nums[i] & bit) != 0) {
                    count[j]++;
                }
                bit = bit << 1;
            }
        }

        int res = 0;
        for (int i = 0; i < 32; i++) {
            res <<= 1;
            // 对 3 求余，若结果为 1， 表示所求的数，第 i 位 为 1
            if (count[i] % 3 == 1) {
                res |= 1;
            }
        }
        return res;
    }


}
