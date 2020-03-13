package com.dream.sum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fanrui
 * @time 2020-03-13 00:11:51
 * 三数之和： 查找 三数之和 为 0 的子序列，要求子序列不重复
 * LeetCode 15： https://leetcode-cn.com/problems/3sum/
 */
public class ThreeSum {

    // 思路：窗口
    public List<List<Integer>> threeSum(int[] nums) {

        ArrayList<List<Integer>> res = new ArrayList<>();
        if (nums == null || nums.length < 3) {
            return res;
        }

        Arrays.sort(nums);

        for (int i = 0; i < nums.length; i++) {
            int L = i + 1;
            int R = nums.length - 1;
            // 最小的值大于0，最大值小于0，直接退出即可
            if (nums[i] > 0 || nums[R] < 0) {
                return res;
            }
            // i 向右移
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue; // 去重
            }

            while (L < R) {
                if (nums[R] < 0) {
                    break;
                }
                // == 0，符合规则，记录结果，L 右移，R 左移
                if (nums[i] + nums[L] + nums[R] == 0) {
                    res.add(Arrays.asList(nums[i], nums[L], nums[R]));
                    L = indexMove(nums, L, false);
                    R = indexMove(nums, R, true);
                } else if (nums[i] + nums[L] + nums[R] < 0) {
                    // < 0，结果太小了，L 右移
                    L = indexMove(nums, L, false);
                } else {
                    // > 0，结果太大了，R 左移
                    R = indexMove(nums, R, true);
                }
            }
        }
        return res;
    }


    private int indexMove(int[] nums, int index, boolean isLeftMove) {
        int curData = nums[index];
        int res = index;
        // 向左移
        if (isLeftMove) {
            for (int i = index - 1; i >= 0; i--) {
                res = i;
                if (nums[i] < curData) {
                    return res;
                }
            }
            return res;
        }
        // 向右移
        for (int i = index + 1; i < nums.length; i++) {
            res = i;
            if (nums[i] > curData) {
                return res;
            }
        }
        return res;
    }


    public static void main(String[] args) {
        System.out.print(new ThreeSum().indexMove(new int[]{0, 0, 0}, 0, false));
    }

}
