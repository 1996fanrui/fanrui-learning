package com.dream.base.array;

/**
 * @author fanrui
 * @time 2020-03-22 02:03:15
 * 945. 使数组唯一的最小增量
 * LeetCode 945：https://leetcode-cn.com/problems/minimum-increment-to-make-array-unique/
 */
public class MinIncrementForUnique {


    //  思路三：
    public int minIncrementForUnique1(int[] A) {
        if (A == null || A.length <= 1) {
            return 0;
        }
        // count 数组中记录所有数据出现了几次。i 位置记录 i 出现了几次。
        int[] count = new int[A.length + 40000];
        int maxData = A[0];
        int minData = A[0];
        for (int i = 0; i < A.length; i++) {
            count[A[i]]++;
            maxData = Math.max(maxData, A[i]);
            minData = Math.min(minData, A[i]);
        }

        // 当前重复的数据个数，即 后续需要找到几个空位置。
        int curRepectCount = 0;
        int res = 0;
        for (int i = minData; i < count.length; i++) {
            if (count[i] > 1) {
                curRepectCount += count[i] - 1;
                res -= i * (count[i] - 1);
                continue;
            }

            if (count[i] == 0 && curRepectCount > 0) {
                curRepectCount--;
                res += i;
            }

            // 遍历过程中，记录原始数据的最大值。第二轮遍历时，如果超过了最大值，
            // 且 curRepectCount ==0，表示不需要在后续找数，直接 结束循环。
            if (curRepectCount == 0 && i > maxData) {
                break;
            }

        }
        return res;
    }

    //  思路四：思路二基础上，不用每次 +1
    public int minIncrementForUnique(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int[] status = new int[50000];
        int counts = 0;

        for (int num : nums) {
            status[num]++;
        }

        for (int i = 0; i < 50000; i++) {
            if (status[i] > 1) {
                int expect = status[i] - 1;
                // 记录 move 次数
                counts += expect;
                // 将 i 位置多余的数，累加到 i+1 位置。
                status[i + 1] += expect;
            }
        }

        return counts;
    }


}
