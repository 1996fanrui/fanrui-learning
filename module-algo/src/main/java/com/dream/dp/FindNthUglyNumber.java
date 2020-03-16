package com.dream.dp;

import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * @author fanrui
 * 264： 找第 n 个丑数
 * LeetCode 264: https://leetcode-cn.com/problems/ugly-number-ii/
 */
public class FindNthUglyNumber {

    // 思路一：minHeap 的思路来做
    private static class UglyHeap {
        int[] nums = new int[1690];
        private HashSet<Long> set = new HashSet<>();

        UglyHeap() {
            // 小顶堆，将 1 入堆，且假如到 set，表示已入堆
            PriorityQueue<Long> minHeap = new PriorityQueue<>();
            minHeap.add(1L);
            set.add(1L);

            int[] primes = new int[]{2, 3, 5};
            for (int i = 0; i < 1690; i++) {
                // 拿出当前最小的 丑数
                long curUgly = minHeap.poll();
                nums[i] = (int) curUgly;
                // 将当前数，*2、*3、*5 加入到 minHeap 中（需要去重）
                for (int j = 0; j < primes.length; j++) {
                    long newUgly = curUgly * primes[j];
                    if (!set.contains(newUgly)) {
                        minHeap.add(newUgly);
                        set.add(newUgly);
                    }
                }
            }
        }
    }

    private static UglyHeap uglyHeap = new UglyHeap();

    public int nthUglyNumber1(int n) {
        return uglyHeap.nums[n - 1];
    }

    // 思路 2：dp
    private static class UglyDP {
        int[] nums = new int[1690];

        UglyDP() {

            nums[0] = 1;
            int i2 = 0;
            int i3 = 0;
            int i5 = 0;
            for (int i = 1; i < 1690; i++) {
                int curUgly = Math.min(Math.min(nums[i2] * 2, nums[i3] * 3), nums[i5] * 5);
                nums[i] = curUgly;
                if (curUgly == nums[i2] * 2) {
                    i2++;
                }
                if (curUgly == nums[i3] * 3) {
                    i3++;
                }
                if (curUgly == nums[i5] * 5) {
                    i5++;
                }
            }
        }
    }

    private static UglyDP uglyDP = new UglyDP();

    public int nthUglyNumber(int n) {
        return uglyDP.nums[n - 1];
    }

}
