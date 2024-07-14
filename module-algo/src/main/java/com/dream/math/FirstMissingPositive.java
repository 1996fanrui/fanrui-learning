package com.dream.math;

/**
 * 41. 缺失的第一个正数
 * https://leetcode.cn/problems/first-missing-positive/
 */
public class FirstMissingPositive {

    public int firstMissingPositive(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            while (true) {
                int curValue = nums[i];
                // The value of current index is expected, so skip
                if (curValue == i + 1) {
                    break;
                }

                // The value of current index is out of range, so skip
                if (curValue < 1 || curValue > n) {
                    break;
                }

                // Swap curIndex and index(curValue-1)
                // Note: curValue may be repeated.
                if (nums[curValue - 1] == curValue) {
                    break;
                }
                nums[i] = nums[curValue - 1];
                nums[curValue - 1] = curValue;
            }
        }

        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        return n + 1;
    }
}
