package com.dream.base.array;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * 56： 合并区间
 * https://leetcode.cn/problems/merge-intervals
 */
public class MergeInterval {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));
        LinkedList<int[]> result = new LinkedList<>();


        for (int i = 0; i < intervals.length; i++) {
            int[] cur = intervals[i];
            if (i == 0) {
                result.add(new int[]{cur[0], cur[1]});
                continue;
            }

            int[] last = result.peekLast();
            if (last[1] < cur[0]) {
                result.add(new int[]{cur[0], cur[1]});
            } else {
                last[1] = Math.max(last[1], cur[1]);
            }
        }
        return result.toArray(new int[result.size()][0]);
    }
}
