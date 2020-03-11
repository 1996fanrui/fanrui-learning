package com.dream.sum.subarray;

import java.util.HashMap;

/**
 * @author fanrui
 * 和为K的子数组个数
 * LeetCode 560：https://leetcode-cn.com/problems/subarray-sum-equals-k/
 */
public class NumOfSubarraySumEqK {

    public int subarraySum(int[] nums, int k) {

        if (nums == null || nums.length == 0) {
            return 0;
        }

        HashMap<Integer, Integer> map = new HashMap<>();
        int res = 0;
        int curSum = 0;
        map.put(0, 1);
        for (int i = 0; i < nums.length; i++) {
            curSum += nums[i];
            if (map.containsKey(curSum - k)) {
                res += map.get(curSum - k);
            }
            int curCount = map.getOrDefault(curSum, 0);
            map.put(curSum, ++curCount);
        }
        return res;
    }

}
