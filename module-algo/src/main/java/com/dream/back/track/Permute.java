package com.dream.back.track;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 全排列
 * 46. https://leetcode.cn/problems/permutations
 */
public class Permute {

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new LinkedList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }

        boolean[] flag = new boolean[nums.length];
        recursion(new LinkedList<>(), flag, nums, result);

        return result;
    }

    private void recursion(LinkedList<Integer> cur, boolean[] flag, int[] nums, List<List<Integer>> result) {
        if (cur.size() == nums.length) {
            result.add(new ArrayList<>(cur));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (flag[i]) {
                continue;
            }
            cur.add(nums[i]);
            flag[i] = true;
            recursion(cur, flag, nums, result);
            flag[i] = false;
            cur.pollLast();
        }
    }
}
