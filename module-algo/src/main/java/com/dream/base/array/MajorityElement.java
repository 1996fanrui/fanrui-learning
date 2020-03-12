package com.dream.base.array;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author fanrui
 * 求众数
 * LeetCode 169：https://leetcode-cn.com/problems/majority-element/
 *
 */
public class MajorityElement {

    // 思路一：维护 map 计数即可
    public int majorityElement(int[] nums) {
        int median = nums.length >> 1;
        HashMap<Integer,Integer> map = new HashMap(median);
        for(int num: nums){
            int count = map.getOrDefault(num,0) + 1;
            if( count > median ){
                return num;
            }
            map.put(num,count);
        }
        return 0;
    }


    // 思路2： 排序求中位数
    public int majorityElement2(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length >> 1];
    }


    // 思路3： 分治思想
    public int majorityElement3(int[] nums) {
        return majorityElementRec(nums, 0, nums.length-1);
    }


    // 数组 nums，求 从 start 到 end 区间内的 众数
    private int majorityElementRec(int[] nums, int start, int end){
        if(start==end){
            return nums[start];
        }
        int mid = start + ((end-start)>>1);

        int left = majorityElementRec(nums, start, mid);
        int right = majorityElementRec(nums, mid+1, end);

        if(left == right){
            return left;
        }

        int leftCount = majorityElementCount(nums, start, mid, left);
        int rightCount = majorityElementCount(nums, mid+1, end, right);

        return leftCount > rightCount ? left : right;
    }


    private int majorityElementCount(int[] nums, int start, int end, int data){
        int res = 0;
        for (int i = start; i <= end; i++) {
            if(nums[i] == data){
                res++;
            }
        }
        return res;
    }


    // 思路4： Boyer-Moore 投票算法
    public int majorityElement4(int[] nums) {

        int count = 1;
        int candidate = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if(count == 0){
                candidate = nums[i];
            }
            count += (nums[i] == candidate) ? 1 : -1;
        }
        return candidate;
    }


}
