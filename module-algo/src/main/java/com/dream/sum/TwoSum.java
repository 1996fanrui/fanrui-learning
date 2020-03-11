package com.dream.sum;

import java.util.HashMap;

/**
 * @author fanrui
 * 两数之和
 * LeetCode 1：https://leetcode-cn.com/problems/two-sum/
 */
public class TwoSum {

    public int[] twoSum(int[] nums, int target) {

        int[] aa = new int[2];

        HashMap<Integer,Integer> map = new HashMap(nums.length);
        for(int i = 0; i< nums.length;i++){
            if(map.containsKey(target - nums[i])){
                aa[0] = map.get(target-nums[i]);
                aa[1] = i;
                break;
            } else {
                map.put(nums[i],i);
            }
        }

        return aa;
    }
}
